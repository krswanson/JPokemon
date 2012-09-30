package jpkmn.exe.gui.world;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import jpkmn.exe.gui.GameWindow;
import jpkmn.game.player.Player;
import jpkmn.game.player.PlayerRegistry;
import jpkmn.game.pokemon.Pokemon;

public class CenterButton extends JButton implements ActionListener {
  public CenterButton(WorldView view) {
    super("Pokemon Center");

    _window = view.window;

    setFocusable(false);
    addActionListener(this);
  }

  @Override
  public void actionPerformed(ActionEvent arg0) {
    // TODO the right way
    Player player = PlayerRegistry.get(_window.playerID());

    for (Pokemon p : player.party) {
      p.healDamage(p.stats.hp.max());
    }

    player.screen.notify("Your Pokemon have been fully healed!",
        "Please come again!");
  }

  private GameWindow _window;
  private static final long serialVersionUID = 1L;
}