
import java.io.Serializable;
import java.util.HashMap;

public class Record implements Serializable{
    public int rid;
    public int[] tokens;
    public Integer[] prefixTokens;


    public Record(String raw) {
        try {
            String[] tuple = raw.split("\\s++");
            String[] tokenStr = tuple[1].split(",");
            tokens = new int[tokenStr.length];
            for (int i = 0; i < tokenStr.length; i++) {
                tokens[i] = Integer.parseInt(tokenStr[i]);
            }
            rid = Integer.parseInt(tuple[0]);

            int prefixlenghth = (int) (tokens.length - (tokens.length * 0.8) + 1);
            prefixTokens = new Integer[prefixlenghth];
            for(int position = 0; position < prefixlenghth; position++){
                prefixTokens[position] = tokens[position];
            }
        } catch (Exception e) {

        }
    }


    public int getRid() {
        return rid;
    }

    public int[] getTokens() {
        return tokens;
    }

    public Integer[] getPrefixTokens() {
        return prefixTokens;
    }

    public boolean isSimilarTo(Record o) {
        // WE ASSUME THE TOKENS ARE ORDERED IN ASCENDING ORDER:
        double intersection = 0;
        int[] oTokens = ((Record) o).getTokens();
        if (this.tokens != null && oTokens != null) {
            for (int i = 0; i < this.tokens.length; i++) {
                for (int j = 0; j < oTokens.length; j++) {
                    if (oTokens[j] == this.tokens[i]) {
                        intersection++;
                    }
                }
            }
            if (intersection / ((double) tokens.length + (double) oTokens.length - intersection) >= 0.5) {
                return true;
            }
        }
        return false;
    }
}
