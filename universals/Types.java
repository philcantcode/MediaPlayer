package universals;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

public class Types 
{
	/* Converts from hex to binary, don't use BigInteger because it drops leading zeros */
	private static Map<Character, String> hexToBinMap = new HashMap<>(16, 1);
	private static Map<String, Character> binToHexMap = new HashMap<>(16, 1);
	private static Map<Integer, String> byteToBinMap = new HashMap<>(16, 1);
	
	/* Sizes */
	public static final int BINARY_LEN = 4;
	
	public Types()
	{
		hexToBinMap.put('0', "0000");	hexToBinMap.put('1', "0001");	hexToBinMap.put('2', "0010");
		hexToBinMap.put('3', "0011");	hexToBinMap.put('4', "0100");	hexToBinMap.put('5', "0101");
		hexToBinMap.put('6', "0110");	hexToBinMap.put('7', "0111");	hexToBinMap.put('8', "1000");
		hexToBinMap.put('9', "1001");	hexToBinMap.put('A', "1010");	hexToBinMap.put('B', "1011");
		hexToBinMap.put('C', "1100");	hexToBinMap.put('D', "1101");	hexToBinMap.put('E', "1110");
		hexToBinMap.put('F', "1111");
		
		byteToBinMap.put(0, "0000");	    byteToBinMap.put(1, "0001");		byteToBinMap.put(2, "0010");
		byteToBinMap.put(3, "0011");	    byteToBinMap.put(4, "0100");		byteToBinMap.put(5, "0101");
		byteToBinMap.put(6, "0110");	    byteToBinMap.put(7, "0111");		byteToBinMap.put(8, "1000");
		byteToBinMap.put(9, "1001");	    byteToBinMap.put(10, "1010");		byteToBinMap.put(11, "1011");
		byteToBinMap.put(12, "1100");		byteToBinMap.put(13, "1101");		byteToBinMap.put(14, "1110");
		byteToBinMap.put(15, "1111");
		
		for (Character k : hexToBinMap.keySet())
		{
			binToHexMap.put(hexToBinMap.get(k), k);
		}
	}
	
	// Convert a hex string to binary string
	public static String hexToBin(String hexStr) 
	{	
		final int len = hexStr.length();
		
	    final StringBuilder binStr = new StringBuilder(len * BINARY_LEN);
	    
	    for(int i = 0; i < len; i++)
	    {
	        binStr.append(hexToBinMap.get(hexStr.charAt(i)));
	    }
	    	    
	    return binStr.toString();
	}
	
	public static String binToHex(String binStr)
	{
		final int len = binStr.length();
		final int pad = len % BINARY_LEN;
		
		final StringBuilder binChars = new StringBuilder(BINARY_LEN);
		final StringBuilder hexString = new StringBuilder(len / BINARY_LEN);
		
		if (pad != 0)
		{
			binStr = Utils.leftPad(binStr, len + BINARY_LEN - pad); // Pad out any missing bit values
		}
		
		char[] binArr = binStr.toCharArray();
		
	    for (int i = 0; i < len; i += BINARY_LEN)
	    {
	    	binChars.append(binArr[i]);
	    	binChars.append(binArr[i + 1]);
	    	binChars.append(binArr[i + 2]);
	    	binChars.append(binArr[i + 3]);
	    		    	
	    	hexString.append(binToHexMap.get(binChars.substring(0, BINARY_LEN)));
	    	binChars.delete(0, BINARY_LEN);
	    }
	    	    
	    return hexString.toString();
	}
	
	// Convert a hex string to ASCII string
	public static String hexToAscii(String hexStr)
	{
		StringBuilder asciiStr = new StringBuilder();
		
	    for (int i = 0; i < hexStr.length(); i += 2) 
	    {
	        asciiStr.append((char) Integer.parseInt(hexStr.substring(i, i + 2), 16));
	    }
	    
	    return asciiStr.toString();
	}
	
	public static String binToAscii(String binStr)
	{
		int len = binStr.length();

		binStr = Utils.leftPad(binStr, (8 - (len % 8)));
		StringBuilder ascii = new StringBuilder(binStr.length() / 8);

		char[] chars = binStr.toCharArray();
		
		for (int j = 0; j < chars.length; j+=8) {
		    int idx = 0;
		    int sum = 0;
		    //for each bit in reverse
		    for (int i = 7; i>= 0; i--) {
		        if (chars[i+j] == '1') {
		            sum += 1 << idx;
		        }
		        idx++;
		    }
		    
		    ascii.append(Character.toChars(sum));
		}
		
		return ascii.toString();
	}
	
	public static char[] hexToCharArr(String hexStr)
	{
		char[] hexArr = new char[hexStr.length() / 2];
		int count = 0;
		
	    for (int i = 0; i < hexStr.length(); i += 2) 
	    {
	        String str = hexStr.substring(i, i + 2);
	        hexArr[count] = (char) Integer.parseInt(str, 16);
	        count++;
	    }
	    
	    return hexArr;
	}
	
	public static String hexToIP(String hexStr)
	{
		try 
		{
			if (hexStr.length() == 8)
				return Inet4Address.getByAddress(DatatypeConverter.parseHexBinary(hexStr)).getHostAddress();
			else if (hexStr.length() == 32)
				return Inet6Address.getByAddress(DatatypeConverter.parseHexBinary(hexStr)).getHostAddress();
			else
				Utils.exit("Types.java", "IP length invalid in hexToIP");
		} 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static int hexToNum(String hexStr)
	{
		return Integer.parseInt(hexStr, 16); 
	}
	
	// Convert a hex string to byte array
	public static byte[] hexToBytes(String hexStr) 
	{
	    int len = hexStr.length();
	    byte[] hexBytes = new byte[len / 2];
	    
	    for (int i = 0; i < len; i += 2) 
	    {
	        hexBytes[i / 2] = (byte) ((Character.digit(hexStr.charAt(i), 16) << 4) + Character.digit(hexStr.charAt(i + 1), 16));
	    }
	    
	    return hexBytes;
	}
	
	// Convert Hex string to MAC address format
	public static String hexToMac(String hexMac)
	{
		StringBuilder mac = new StringBuilder(hexMac);
				
		for(int i = 2; i < hexMac.length( ) + (i / 3); i += 3)
		{
			mac.insert(i, ':');
		}
		
		return mac.toString();
	}
	
	public static String bytesToHex(byte[] bytes) 
	{
		char[] hexArray = "0123456789ABCDEF".toCharArray();
	    char[] hexChars = new char[bytes.length * 2];
	    
	    for ( int j = 0; j < bytes.length; j++ ) 
	    {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    
	    return new String(hexChars);
	}
	
	public static String numToHex(int num)
	{
		return Integer.toHexString(num);
	}
	
	public static int byteArrayToInteger(byte[] b, ByteOrder order)
	{
		ByteBuffer wrapped = ByteBuffer.wrap(b);
		wrapped.order(order);
		
		return wrapped.getInt();
	}
	
	public static String ipToHex(String ipStr)
	{
		StringBuilder hex = new StringBuilder();
		String[] part = ipStr.split("[\\.,]");
		
		if (part.length < 4) 
		{
			return "00000000";
		}
		
		for (int i = 0; i < 4; i++) 
		{
			int decimal = Integer.parseInt(part[i]);
			
			if (decimal < 16) // Append a 0 to maintian 2 digits for every
			{
				hex.append("0" + String.format("%01X", decimal));
			} 
			else 
			{
				hex.append(String.format("%01X", decimal));
			}
		}
		
		return hex.toString();
	}
	
	public static String bytesToBin(byte[] bytes)
	{
		StringBuilder sb = new StringBuilder(bytes.length * 8);
		
		for (int i = 0; i < bytes.length; i++)
		{
			sb.append(byteToBinMap.get((bytes[i] & 0xFF) >> 4));
			sb.append(byteToBinMap.get((bytes[i] & 0xFF) & 0x0F));
		}
		
		return sb.toString();
	}
}
