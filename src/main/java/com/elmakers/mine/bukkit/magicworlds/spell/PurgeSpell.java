package com.elmakers.mine.bukkit.magicworlds.spell;

import java.util.Collection;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.elmakers.mine.bukkit.api.spell.SpellResult;
import com.elmakers.mine.bukkit.spell.UndoableSpell;
import com.elmakers.mine.bukkit.utility.Target;

public class PurgeSpell extends UndoableSpell {

	@Override
	public SpellResult onCast(ConfigurationSection parameters) {
		Target target = getTarget();
		if (!target.hasEntity()) {
			return SpellResult.NO_TARGET;
		}
		Entity targetEntity = target.getEntity();

		if (targetEntity instanceof Player) {
			Player targetPlayer = (Player)targetEntity;
			targetPlayer.kickPlayer(getMessage("cast_player_message"));
		} else {
			Collection<? extends Entity> entities = targetEntity.getWorld().getEntitiesByClass(targetEntity.getClass());
			for (Entity entity : entities) {
                if (controller.isNPC(entity)) continue;
				registerForUndo(entity);
				entity.remove();
			}
		}
		registerForUndo();
		return SpellResult.CAST;
	}
}
