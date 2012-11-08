package jpkmn.game.pokemon.move;

import java.util.ArrayList;
import java.util.List;

import jpkmn.Constants;
import jpkmn.game.pokemon.move.effect.MoveEffectInfo;

import com.kremerk.Sqlite.DataConnectionException;
import com.kremerk.Sqlite.DataConnectionManager;
import com.kremerk.Sqlite.SqlStatement;
import com.kremerk.Sqlite.Annotations.OneToMany;
import com.kremerk.Sqlite.Annotations.PrimaryKey;

public class MoveInfo {
  @PrimaryKey
  private int number;

  private String name;
  private double accuracy;
  private int type, power, pp, style;

  @OneToMany("move_number")
  private List<MoveEffectInfo> effects = new ArrayList<MoveEffectInfo>();

  private static MoveInfo[] cache = new MoveInfo[Constants.MOVENUMBER];

  public static MoveInfo get(int number) {
    DataConnectionManager.init("data/Pokemon.db");

    if (cache[number - 1] == null) {
      try {
        List<MoveInfo> moves = SqlStatement.select(MoveInfo.class)
            .where("number").eq(number).getList();

        cache[number - 1] = moves.isEmpty() ? null : moves.get(0);
      } catch (DataConnectionException e) {
        e.printStackTrace();
        return null;
      }
    }

    return cache[number - 1];
  }

  //@preformat
  public int getNumber() {return number;} public void setNumber(int n) {number = n;}
  public String getName() {return name;} public void setName(String s) {name = s;}
  public double getAccuracy() {return accuracy;} public void setAccuracy(double d) {accuracy = d;}
  public int getType() {return type;} public void setType(int t) {type = t;}
  public int getPower() {return power;} public void setPower(int p) {power = p;}
  public int getPp() {return pp;} public void setPp(int p) {pp = p;}
  public int getStyle() {return style;} public void setStyle(int s) {style = s;}
  public List<MoveEffectInfo> getEffects() {return effects;} public void setEffects(List<MoveEffectInfo> l) {effects = l;}
  //@format
}