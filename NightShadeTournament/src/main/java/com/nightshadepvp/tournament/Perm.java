package com.nightshadepvp.tournament;

import com.massivecraft.massivecore.Identified;
import com.massivecraft.massivecore.util.PermissionUtil;
import org.bukkit.permissions.Permissible;

public enum Perm implements Identified
{
	BASECOMMAND,
	CREATE,
	RELOAD,
	REMOVE,
	SETSPAWN1,
	SETSPAWN2,
	TELEPORT,
	LIST,
	SET_DISPLAY,
	CLAIM_HOST,
	SEED,
	SET_KIT,
	START,
	SET_SPAWN,
	SLOTS,
	SET_EDIT_LOCATION,
	EDIT,
	SAVE_KIT,
	VIEW_PLAYER_INVENTORY,
	FREEZE_CHAT,
	WHITELIST,
	ON,
	ADD,
	CLEAR,
	OFF,
	HELPOP,
	STATS,
	POST,
	FINISH,
	FORCEWINNER,
	GIVEKIT,
	VERSION;
	
	private final String id;
	
	Perm()
	{
		this.id = PermissionUtil.createPermissionId(Tournament.get(), this);
	}
	
	@Override
	public String getId()
	{
		return this.id;
	}
	
	public boolean has(Permissible permissible, boolean verboose)
	{
		return PermissionUtil.hasPermission(permissible, this, verboose);
	}
	
	public boolean has(Permissible permissible)
	{
		return PermissionUtil.hasPermission(permissible, this);
	}
}
