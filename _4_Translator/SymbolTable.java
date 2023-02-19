package _4_Translator;
import java.util.*;

public class SymbolTable {

    Map <String, Integer> OffsetMap = new HashMap <String,Integer>();
    private int count;

	public void insert(String s, int address) {
        if(address >= 0 && !OffsetMap.containsValue(address))
            OffsetMap.put(s,address);
        else throw new IllegalArgumentException("Reference to a memory location already occupied by another variable");
	}

	public int lookupAddress ( String s ) {
            if(OffsetMap.containsKey(s)) return OffsetMap.get(s);
            else return -1;
	}

    public int insertIf(String s) {
        int val = lookupAddress(s);
        if(val == -1) {
            val = count++;
            insert(s, val);
        }
        return val;
    }
}
