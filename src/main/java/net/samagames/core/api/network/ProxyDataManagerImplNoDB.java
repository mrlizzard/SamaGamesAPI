package net.samagames.core.api.network;

import net.samagames.api.network.ProxyDataManager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by zyuiop
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class ProxyDataManagerImplNoDB implements ProxyDataManager {
	@Override
	public Set<UUID> getPlayersOnServer(String server) {
		return new HashSet<>();
	}

	@Override
	public Set<UUID> getPlayersOnProxy(String server) {
		return new HashSet<>();
	}
}