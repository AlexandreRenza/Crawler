import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

class StartAppScreen extends JFrame implements ActionListener {


    public static JButton btn = new JButton("Start");

    public void screen(){

        JFrame frame = new JFrame("Crawler");
        JPanel panel = new JPanel();
        frame.setSize(300, 300);

        JTextField txt = new JTextField("", 10);
        JLabel lb = new JLabel("How many Topic would like to get?(1-37)");

        panel.add(txt);
        panel.add(btn);
        panel.add(lb);

        frame.add(panel);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);

        txt.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyChar() >= '0' && ke.getKeyChar() <= '9' || ke.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    txt.setEditable(true);
                    lb.setText("How many Topic would like to get?(1-37)");
                } else {
                    txt.setEditable(false);
                    lb.setText("* Enter only numeric digits(0-9)");
                }
            }
        });


        StartAppScreen.btn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if(txt.getText().trim().length() != 0) {


                    if (Integer.parseInt(txt.getText().trim()) >= 1 && Integer.parseInt(txt.getText().trim()) <= 37) {

                        JFrame frame2 = new JFrame("Processing...");
                        JPanel panel2 = new JPanel();
                        frame2.setSize(300, 300);
                        frame2.add(panel2);
                        frame2.setVisible(true);
                        frame2.setLocationRelativeTo(null);

                        // Start the Crawler
                        String url_topics = "http://www.cochranelibrary.com/home/topic-and-review-group-list.html?page=topic";
                        Crawler crawler = new Crawler(url_topics, Integer.parseInt(txt.getText().trim()));
                        try {
                            crawler.StartSearch();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }


                        JLabel hyperlink = new JLabel("File Generated: "+ Paths.get("./cochrane_reviews.txt").toString());
                        hyperlink.setForeground(Color.BLUE.darker());
                        hyperlink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        hyperlink.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent mouseEvent) {
                                try {
                                    String filePath = Paths.get("./cochrane_reviews.txt").toString();
                                    Desktop.getDesktop().open(new File(filePath));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        frame2.setTitle("Process Finished");
                        panel2.add(hyperlink);

                    } else{
                        lb.setText("* We only have 37 topics available.");
                    }
                }
            }
        });
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {

    }
}
