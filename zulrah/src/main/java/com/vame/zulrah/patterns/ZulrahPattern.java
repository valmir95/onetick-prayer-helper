/*
 * Copyright (c) 2017, Aria <aria@ar1as.space>
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
 * Copyright (c) 2017, Devin French <https://github.com/devinfrench>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.vame.zulrah.patterns;

import java.util.ArrayList;
import java.util.List;

import com.vame.zulrah.phase.StandLocation;
import com.vame.zulrah.phase.ZulrahLocation;
import com.vame.zulrah.phase.ZulrahPhase;
import com.vame.zulrah.phase.ZulrahType;
import net.runelite.api.Prayer;


public abstract class ZulrahPattern
{
	private final List<ZulrahPhase> pattern = new ArrayList<>();

	final void add(ZulrahLocation loc, ZulrahType type, StandLocation standLocation, Prayer prayer)
	{
		add(loc, type, standLocation, false, prayer);
	}

	final void addJad(ZulrahLocation loc, ZulrahType type, StandLocation standLocation, Prayer prayer)
	{
		add(loc, type, standLocation, true, prayer);
	}

	private void add(ZulrahLocation loc, ZulrahType type, StandLocation standLocation, boolean jad, Prayer prayer)
	{
		pattern.add(new ZulrahPhase(loc, type, jad, standLocation, prayer));
	}

	public ZulrahPhase get(int index)
	{
		if (index >= pattern.size())
		{
			return null;
		}

		return pattern.get(index);
	}

	public int getJadIndex(){
		int counter = 0;
		for (ZulrahPhase phase : this.pattern){
			if(phase.isJad()){
				return counter;
			}
			counter++;
		}
		return -1;
	}

	public boolean stageMatches(int index, ZulrahPhase instance)
	{
		ZulrahPhase patternInstance = get(index);
		return patternInstance != null && patternInstance.equals(instance);
	}

	public boolean canReset(int index)
	{
		return index >= pattern.size();
	}
}