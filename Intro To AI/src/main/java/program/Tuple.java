package program;

import org.json.JSONObject;

/**
 * Created by Gur on 12/27/2016.
 */
public class Tuple {
    public int score;
    public String action;

    public Tuple(int score, String action){
        this.score = score;
        this.action = action;
    }

    public String toString(){
        return new JSONObject().put("score", score).put("action", action).toString();
    }
}
