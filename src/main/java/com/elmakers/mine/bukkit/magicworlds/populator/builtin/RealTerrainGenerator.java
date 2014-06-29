package com.elmakers.mine.bukkit.magicworlds.populator.builtin;

import java.awt.geom.Point2D;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.zip.GZIPInputStream;

import com.elmakers.mine.bukkit.magicworlds.populator.MagicChunkPopulator;
import com.elmakers.mine.bukkit.utility.Mercator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.ViewType;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.geometry.DirectPosition2D;

public class RealTerrainGenerator extends MagicChunkPopulator {
    private static final String SRTM_FTP_SERVER = "ftp.glcf.umd.edu";
    private static final int SRTM_FTP_PORT = 21;
    private static final String SRTM_FTP_PATH = "/glcf/SRTM/Degree_Tiles";
    private static final String SRTM_FTP_USER = "anonymous";
    private static final String SRTM_FTP_PWD = "";

    private static final double METERS_PER_DEGREE = 111319.9;

    private int maxY = 255;
    private int seaLevel = 64;
    private int minY = 0;
    private double originLatitude = 0;
    private double originLongitude = 0;
    private double minElevation = -100;
    private double maxElevation = 1000;
    private double zScale = 1;
    private double xScale = 1;
    private double xOffset = 0;
    private double zOffset = 0;
    DecimalFormat formatter = new DecimalFormat("#.0000000000");
    DecimalFormat fileFormatter = new DecimalFormat("000");

    // TODO: Cache control!
    private final Map<String, GridCoverage2D> cache = new HashMap<String, GridCoverage2D>();

    @Override
    public boolean onLoad(ConfigurationSection config) {

        maxY = config.getInt("max_y", maxY);
        minY = config.getInt("min_y", minY);
        seaLevel = config.getInt("sea_level", seaLevel);
        minElevation = config.getDouble("min_elevation", minElevation);
        maxElevation = config.getDouble("max_elevation", maxElevation);
        originLatitude = config.getDouble("origin_latitude", originLatitude);
        originLongitude = config.getDouble("origin_longitude", originLongitude);

        xOffset = Mercator.lon2x(Math.abs(originLongitude)) * METERS_PER_DEGREE * Math.signum(originLongitude);
        zOffset = Mercator.lat2y(Math.abs(originLatitude)) * METERS_PER_DEGREE * Math.signum(originLatitude);

        xScale = config.getDouble("scale_x", xScale);
        zScale = config.getDouble("scale_z", zScale);

        controller.getLogger().info("Origin at: " + originLongitude + ", " + originLatitude + " => " + "(" + xOffset + "," + zOffset + ")");

        return true;
    }

    public int getElevation(int x, int z) {
        double latitude = toLatitude(x);
        double longitude = toLongitude(z);

        char latHemo = latitude <= -1 ? 's' : 'n';
        char lonHemo = longitude <= -1 ? 'w' : 'e';
        latitude = Math.abs(latitude);
        longitude = Math.abs(longitude);
        String latIndex = fileFormatter.format((int)latitude);
        String lonIndex = fileFormatter.format((int)longitude);

        GridCoverage2D reader = null;
        String chunkKey = "SRTM_f03_"  + latHemo + latIndex + lonHemo + lonIndex;
        if (!cache.containsKey(chunkKey)) {
            String filePath = latHemo + latIndex + "/" + chunkKey;
            String fileName = chunkKey + ".tif";
            double[][] data = null;

            File dataFolder = new File(controller.getPlugin().getDataFolder() + "/data/" + filePath);
            dataFolder.mkdirs();
            File cacheFile = new File(dataFolder, fileName);
            controller.getLogger().info("Checking file cache: " + cacheFile.getAbsolutePath());
            if (!cacheFile.exists()) {
                FTPClient ftpClient = new FTPClient();
                try {
                    controller.getLogger().info("Connecting to " + SRTM_FTP_SERVER + ":" + SRTM_FTP_PORT);
                    ftpClient.connect(SRTM_FTP_SERVER, SRTM_FTP_PORT);
                    ftpClient.login(SRTM_FTP_USER, SRTM_FTP_PWD);
                    ftpClient.enterLocalPassiveMode();
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

                    String remoteFile = SRTM_FTP_PATH + "/" + filePath + "/" + fileName + ".gz";
                    controller.getLogger().info("Downloading: " + remoteFile);
                    OutputStream outputStream2 = new BufferedOutputStream(new FileOutputStream(cacheFile));
                    InputStream inputStream = new GZIPInputStream(ftpClient.retrieveFileStream(remoteFile));
                    if (inputStream == null) {
                        controller.getLogger().warning("Failed to download " + remoteFile);
                    } else {
                        byte[] bytesArray = new byte[4096];
                        int bytesRead = -1;
                        while ((bytesRead = inputStream.read(bytesArray)) != -1) {
                            outputStream2.write(bytesArray, 0, bytesRead);
                        }

                        if (!ftpClient.completePendingCommand()) {
                            controller.getLogger().warning("Failed to complete download " + remoteFile);
                        }
                        inputStream.close();
                    }
                    outputStream2.close();
                    controller.getLogger().info("... done.");

                } catch (Exception ex) {
                    controller.getLogger().warning("Error: " + ex.getMessage());
                    ex.printStackTrace();
                } finally {
                    try {
                        if (ftpClient.isConnected()) {
                            ftpClient.logout();
                            ftpClient.disconnect();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            controller.getLogger().info("Loading: " + cacheFile.getAbsolutePath());

            try {
                AbstractGridFormat format = GridFormatFinder.findFormat(cacheFile);
                GridCoverage2DReader abstractReader = format.getReader(cacheFile);
                Object coverage = abstractReader.read(null);
                if (coverage == null) {
                    throw new Exception("Failed to read coverage");
                }
                if (!(coverage instanceof GridCoverage2D)) {
                    throw new Exception("Reader is not an instance of GridCoverage2D: " + coverage.getClass().getName());
                }
                reader = (GridCoverage2D)coverage;
            } catch (Throwable ex) {
                ex.printStackTrace();
                reader = null;
            }

            if (reader == null) {
                controller.getLogger().warning("Failed to load chunk " + chunkKey);
            }
            cache.put(chunkKey, reader);
        } else {
            reader = cache.get(chunkKey);
        }

        if (reader == null) {
            return seaLevel;
        }

        try {
            GridCoverage2D geoView = reader.view(ViewType.GEOPHYSICS);
            Object values = geoView.evaluate(new DirectPosition2D(longitude, latitude));
            int[] data = (int[]) values;
            return data[0];
        } catch (Exception ex){
            // ex.printStackTrace();
            controller.getLogger().warning("Error: " + ex.getMessage());
            GridCoverage2D geoView = reader.view(ViewType.GEOPHYSICS);
            controller.getLogger().info(" Envelope: " + geoView.getEnvelope2D() + ", request: " + longitude + ", " + latitude);
        }

        return seaLevel;
    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int elevation = getElevation(chunk.getX() * 16 + x, chunk.getZ() * 16 + z);
                for (int y = 2; y < elevation; y++) {
                    chunk.getBlock(x, y, z).setType(Material.DIRT);
                }
            }
        }
    }

    public double fromLongitude(double lon) {
        return METERS_PER_DEGREE * (Mercator.lon2x(lon)) / xScale - xOffset;
    }

    public double fromLatitude(double lat) {
        return METERS_PER_DEGREE * (Mercator.lat2y(lat)) / zScale - zOffset;
    }

    public double toLongitude(double x) {
        return Mercator.x2lon(xScale * (x + xOffset) / METERS_PER_DEGREE);
    }

    public double toLatitude(double z) {
        return Mercator.y2lat(zScale * (z + zOffset) / METERS_PER_DEGREE);
    }
}
