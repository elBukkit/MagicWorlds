package com.elmakers.mine.bukkit.magicworlds.populator.builtin;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

import com.elmakers.mine.bukkit.magicworlds.populator.MagicChunkPopulator;
import com.elmakers.mine.bukkit.utility.Mercator;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class RealTerrainGenerator extends MagicChunkPopulator {
    private static final String SRTM_FTP_SERVER = "ftp.glcf.umd.edu";
    private static final int SRTM_FTP_PORT = 21;
    private static final String SRTM_FTP_PATH = "/glcf/SRTM/Degree_Tiles";
    private static final String SRTM_FTP_USER = "anonymous";
    private static final String SRTM_FTP_PWD = "";

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
    private final Map<String, double[][]> imageCache = new HashMap<String, double[][]>();

    @Override
    public boolean onLoad(ConfigurationSection config) {

        maxY = config.getInt("max_y", maxY);
        minY = config.getInt("min_y", minY);
        seaLevel = config.getInt("sea_level", seaLevel);
        minElevation = config.getDouble("min_elevation", minElevation);
        maxElevation = config.getDouble("max_elevation", maxElevation);
        originLatitude = config.getDouble("origin_latitude", originLatitude);
        originLongitude = config.getDouble("origin_longitude", originLongitude);

        // Need to make sure offset and scale are not set for this to work!
        xOffset = 0;
        zOffset = 0;
        xScale = 1;
        zScale = 1;
        xOffset = fromLongtiude(originLongitude);
        zOffset = fromLatitude(originLatitude);

        xScale = config.getDouble("scale_x", xScale);
        zScale = config.getDouble("scale_z", zScale);

        controller.getLogger().info("Origin at: " + originLongitude + ", " + originLatitude);

        return true;
    }

    public int getElevation(int x, int z) {
        double latitude = toLatitude(x);
        double longitude = toLongitude(z);

        String latIndex = fileFormatter.format((int)latitude);
        String lonIndex = fileFormatter.format((int)longitude);

        // TODO: Other hemispheres
        String chunkKey = "SRTM_f03_n" + latIndex + "w" + lonIndex;
        if (!imageCache.containsKey(chunkKey)) {
            controller.getLogger().info("Fetching data for " + longitude + "," + latitude + " => (" + x + "," + z + ")");

            String filePath = "n" + latIndex + "/" + chunkKey;
            String fileName = chunkKey + ".tif.gz";
            double[][] data = null;

            File dataFolder = new File(controller.getPlugin().getDataFolder() + "/data/" + filePath);
            dataFolder.mkdirs();
            File cacheFile = new File(dataFolder, fileName);
            controller.getLogger().info("Checking for file: " + cacheFile.getAbsolutePath());
            if (!cacheFile.exists()) {
                FTPClient ftpClient = new FTPClient();
                try {
                    controller.getLogger().info("Connecting to " + SRTM_FTP_SERVER + ":" + SRTM_FTP_PORT);
                    ftpClient.connect(SRTM_FTP_SERVER, SRTM_FTP_PORT);
                    ftpClient.login(SRTM_FTP_USER, SRTM_FTP_PWD);
                    ftpClient.enterLocalPassiveMode();
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

                    String remoteFile = SRTM_FTP_PATH + "/" + filePath + "/" + fileName;
                    controller.getLogger().info("Downloading: " + remoteFile);
                    OutputStream outputStream2 = new BufferedOutputStream(new FileOutputStream(cacheFile));
                    InputStream inputStream = ftpClient.retrieveFileStream(remoteFile);
                    byte[] bytesArray = new byte[4096];
                    int bytesRead = -1;
                    while ((bytesRead = inputStream.read(bytesArray)) != -1) {
                        outputStream2.write(bytesArray, 0, bytesRead);
                    }

                    boolean success = ftpClient.completePendingCommand();
                    if (success) {
                        controller.getLogger().info("... done.");
                    }
                    outputStream2.close();
                    inputStream.close();

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

            imageCache.put(chunkKey, data);
        }

        return 100;
    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int elevation = getElevation(chunk.getX() * 16 + x, chunk.getZ() * 16 + z);
                for (int y = 2; y < elevation / maxElevation; y++) {
                    chunk.getBlock(x, y, z).setType(Material.DIRT);
                }
            }
        }/*

        String fileKey = "chunk-" + chunk.getX() + "-" + chunk.getZ();
        File dataFolder = new File(controller.getPlugin().getDataFolder() + "/data");
        dataFolder.mkdirs();
        File cacheFile = new File(dataFolder, fileKey + ".dat");
        controller.getLogger().info("Checking for file: " + cacheFile.getName());

        if (!cacheFile.exists()) {
            int chunkOffsetX = chunk.getX() * 16;
            int chunkOffsetZ = chunk.getZ() * 16;
            String queryString = "https://maps.googleapis.com/maps/api/elevation/json?key=" + controller.getGoogleAPIKey() + "&locations=";
            List<String> points = new ArrayList<String>(16 * 16);
            for (int x = 0; x < 4; x++) {
                for (int z = 0; z < 4; z++) {
                    double lon = toLongitude((double)(x + 0.5) * 4 + chunkOffsetX);
                    double lat = toLatitude((double)(z + 0.5) * 4 + chunkOffsetZ);
                    points.add(formatter.format(lon) + "," + formatter.format(lat));
                }
            }

            queryString = queryString + StringUtils.join(points, '|');
            controller.getLogger().info("Downloading terrain data for chunk " + chunk.getX() + "," + chunk.getZ());

            try {
                URL url = new URL(queryString);
                URLConnection con = url.openConnection();
                InputStream in = con.getInputStream();
                String encoding = con.getContentEncoding();
                encoding = encoding == null ? "UTF-8" : encoding;
                String body = IOUtils.toString(in, encoding);

                PrintWriter out = new PrintWriter(cacheFile);
                out.print(body);
                out.close();
                controller.getLogger().info("..done, saved to: " + cacheFile.getAbsolutePath());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }*/
    }

    public double fromLongtiude(double lon) {
        return (Mercator.lon2x(lon) - xOffset) / xScale;
    }

    public double fromLatitude(double lat) {
        return (Mercator.lat2y(lat) - zOffset) / zScale;
    }

    public double toLongitude(double x) {
        return Mercator.x2lon(xScale * (x + xOffset));
    }

    public double toLatitude(double z) {
        return Mercator.y2lat(zScale * (z + zOffset));
    }
}
