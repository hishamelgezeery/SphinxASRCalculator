package edu.cmu.sphinx.demo.calculator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

public class GUI {

	public static void main(String[] args) {
		new GUI();
	}

	public GUI() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException
						| UnsupportedLookAndFeelException ex) {
				}

				JFrame frame = new JFrame("Sphinx ASR caculator");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setPreferredSize(new Dimension(440, 300));
				frame.setLayout(new BorderLayout());
				TestPane p = new TestPane();
				frame.add(p);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	public class TestPane extends JPanel implements ActionListener {
		Calculator calculator;
		private JButton evaluate;
		private JTextArea inputText;
		private JLabel result;
		private JLabel error;
		private JButton ask;
		private JLabel input;

		public TestPane() {
			calculator = new Calculator();

			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			setBackground(Color.GRAY);
			JPanel titlePanel = new JPanel();
			JPanel speechPanel = new JPanel();
			JPanel summaryPanel = new JPanel();
			JPanel textPanel = new JPanel();

			titlePanel.setBackground(Color.WHITE);
			speechPanel.setBackground(Color.WHITE);
			summaryPanel.setBackground(Color.WHITE);
			textPanel.setBackground(Color.WHITE);

			titlePanel.setMaximumSize(new Dimension(400, 50));
			speechPanel.setMaximumSize(new Dimension(400, 70));
			summaryPanel.setMaximumSize(new Dimension(400, 50));
			textPanel.setMaximumSize(new Dimension(400, 100));

			titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);// 0.0
			speechPanel.setAlignmentX(Component.CENTER_ALIGNMENT);// 0.0
			summaryPanel.setAlignmentX(Component.CENTER_ALIGNMENT);// 0.0
			textPanel.setAlignmentX(Component.CENTER_ALIGNMENT);// 0.0

			// titlePanel content
			JLabel jlabel = new JLabel("Sphinx ASR Calculator");
			jlabel.setFont(new Font("Verdana", 1, 25));
			titlePanel.add(jlabel);

			// speechPanel content
			speechPanel.setLayout(new BorderLayout());
			ask = new JButton("Ask!");
			JPanel temp = new JPanel();
			temp.setBackground(Color.WHITE);
			temp.add(ask);
			input = new JLabel("You said:dkdkjdkjdjkdjkdjkdjk");
			JPanel temp2 = new JPanel();
			temp2.setBackground(Color.WHITE);
			temp2.add(input);
			speechPanel.add(temp, BorderLayout.NORTH);
			speechPanel.add(temp2, BorderLayout.SOUTH);

			// summaryPanel content
			summaryPanel.setLayout(new BorderLayout());
			error = new JLabel("Error:");
			JPanel temp3 = new JPanel();
			temp3.setBackground(Color.WHITE);
			temp3.add(error);
			result = new JLabel("Result = ");
			JPanel temp4 = new JPanel();
			temp4.setBackground(Color.WHITE);
			temp4.add(result);
			summaryPanel.add(temp3, BorderLayout.NORTH);
			summaryPanel.add(temp4, BorderLayout.SOUTH);

			// textPanel content
			textPanel.setLayout(new BorderLayout());
			evaluate = new JButton("Compute!");
			JPanel temp5 = new JPanel();
			temp5.setBackground(Color.WHITE);
			temp5.add(evaluate);
			inputText = new JTextArea(2, 20);
			inputText.setFont(new Font("Verdana", 1, 11));
			inputText.setBackground(Color.LIGHT_GRAY);
			JPanel temp6 = new JPanel();
			temp6.setMinimumSize(new Dimension(200, 50));
			temp6.setBackground(Color.WHITE);
			temp6.add(inputText);
			textPanel.add(temp5, BorderLayout.NORTH);
			textPanel.add(inputText, BorderLayout.SOUTH);
			//
			ask.addActionListener(this);
			evaluate.addActionListener(this);
			// seperators
			JPanel seperator1 = new JPanel();
			seperator1.setBackground(Color.GRAY);
			seperator1.setMaximumSize((new Dimension(400, 10)));

			JPanel seperator2 = new JPanel();
			seperator2.setBackground(Color.GRAY);
			seperator2.setMaximumSize((new Dimension(400, 10)));

			JPanel seperator3 = new JPanel();
			seperator3.setBackground(Color.GRAY);
			seperator3.setMaximumSize((new Dimension(400, 10)));
			add(titlePanel);
			add(titlePanel);
			add(seperator1);
			add(speechPanel);
			add(seperator2);
			add(summaryPanel);
			add(seperator3);
			add(textPanel);

		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			error.setText("No Error");
			error.setForeground(Color.BLACK);
			String text = "";
			if (arg0.getSource() == ask) {
				System.out.println("entered herre");
				calculator.listen();
				System.out.println("finished");
				if (!calculator.errorOccured)
					input.setText("You said: " + calculator.saidSentence);
			}
			if (arg0.getSource() == evaluate) {
				text = inputText.getText();
				if (!text.equals("")) {
					calculator.parse(text);
				}
				if (calculator.errorOccured) {
					error.setText("Error Occurred");
					error.setForeground(Color.RED);
					result.setText("Result = ");
					calculator.errorOccured = false;
				} else {
					result.setText("Result = " + calculator.result);
				}
			}
		}
	}
}