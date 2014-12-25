package xy.command.ui.component;
import java.awt.Adjustable;

 

import javax.swing.SwingUtilities;

 

public class AutoScrollerToMaximum extends Thread {

 

       private Adjustable adjustable;

 

       public AutoScrollerToMaximum(Adjustable adjustable) {

              this.adjustable = adjustable;

       }

 

       public void run() {

              while (true) {

                     if (shouldScroll()) {

                           SwingUtilities.invokeLater(new Runnable() {

 

                                  @Override

                                  public void run() {

                                         adjustable.setValue(adjustable.getMaximum());

                                  }

 

                           });

                     }

                     try {

                           Thread.sleep(500);

                     } catch (InterruptedException e) {

                           Thread.currentThread().interrupt();

                     }

                     if (isInterrupted()) {

                           break;

                     }

              }

       }

 

       public boolean shouldScroll() {

              int minimumValue = adjustable.getValue()

                           + adjustable.getVisibleAmount();

              int maximumValue = adjustable.getMaximum();

              return maximumValue > minimumValue;

       }

 

}

 