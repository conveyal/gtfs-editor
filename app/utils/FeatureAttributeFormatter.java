package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.opengis.feature.simple.SimpleFeature;

import play.Logger;

public class FeatureAttributeFormatter {
	
	String formatString;
	Matcher matches;
	
	public FeatureAttributeFormatter(String format)
	{
		this.formatString = format;
		
		Pattern pattern = Pattern.compile("#([0-9]+)");
		
		this.matches = pattern.matcher(format);
	}
	
	public String format(SimpleFeature feature)
	{
		String output = new String(formatString);
		
		while(matches.find())
		{
			String sub = matches.group();
				
			Integer fieldPosition = Integer.parseInt(sub.replace("#", ""));
			
			try
			{
				String attributeString = feature.getAttribute(fieldPosition).toString();
			
				output = output.replace(sub, attributeString);
			}
			catch(Exception e) {
				Logger.warn("Index out of range.");
			}
			
		}
		
		return output;
	}
}
