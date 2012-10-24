package utils;

import java.math.BigInteger;

public class StopSequence implements Comparable<StopSequence> {
	
	public BigInteger stopId;
	public Integer stopSequence;

	public StopSequence(BigInteger id, Integer sequence)
	{
		stopId = id;
		stopSequence = sequence;
	}

	@Override
	public int compareTo(StopSequence o) {
		
		return this.stopSequence.compareTo(o.stopSequence);
	}
	
}
