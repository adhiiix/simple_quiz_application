package quiz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OnlineQuizApplication {

    protected static final int MAX_QUESTIONS = 10;
	private JFrame frame;
    private JLabel questionLabel;
    private JRadioButton[] options;
    private JButton submitButton;
    private JButton finishButton;
    private Timer timer;
    private JLabel timerLabel;
    private int timeRemaining = 10;

    // Database connection
    private Connection connection;
    
    private int questionCounter = 1;


    public OnlineQuizApplication() {
        initializeUI();
        connectToDatabase();
        loadQuestionFromDatabase(); // Loading the first question from the database
    }

    private void initializeUI() {
        frame = new JFrame("Online Quiz Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 420);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        timerLabel = new JLabel("Time Remaining: " + timeRemaining + " seconds");
        panel.add(timerLabel);

        questionLabel = new JLabel();
        panel.add(questionLabel);

        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timeRemaining--;
                if (timeRemaining < 0) {
                    timer.stop();
                    System.out.println("!! Time Over !! Thanks For Attempting");
                    finishQuiz();
                    //System.exit(0); // Implement logic for when time runs out
                } else {
                    // Show a timer label or display the remaining time
                    timerLabel.setText("Time Remaining: " + timeRemaining + " seconds");
                }
            }
        });
        timer.start();

        options = new JRadioButton[4];
        ButtonGroup optionGroup = new ButtonGroup();
        for (int i = 0; i < 4; i++) {
            options[i] = new JRadioButton("Option " + (i + 1));
            optionGroup.add(options[i]);
            panel.add(options[i]);
        }

        submitButton = new JButton("Next");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitAnswer();
                if (questionCounter <= MAX_QUESTIONS) {
                loadQuestionFromDatabase(); // Loads the next question
            }
                else {
                    System.out.println("Congratulations");
                }
            }
        });
        panel.add(submitButton);

        finishButton = new JButton("Finish");
        finishButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                finishQuiz();
                timer.stop();
            }
        });
        panel.add(finishButton);

        
        frame.add(panel);
        frame.setVisible(true);
    }

    private void connectToDatabase() {
        try {
    
            String url = "jdbc:mysql://localhost:3306/quiz";
            String username = "root";
            String password = "mysql";
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadQuestionFromDatabase() {
        try {
            String query = "SELECT questions, option1, option2, option3, option4 FROM questions WHERE question_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, questionCounter);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                questionLabel.setText(resultSet.getString("questions"));
                for (int i = 0; i < 4; i++) {
                    options[i].setText(resultSet.getString("option" + (i + 1)));
                    
                }
                questionCounter++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    private void submitAnswer() {
        for (int i = 0; i < 4; i++) {
            if (options[i].isSelected()) {
                String selectedOption = options[i].getText();
                JOptionPane.showMessageDialog(frame, "Selected option: " + selectedOption);
                clearOptions();
                break;
            }
        }
    }

    private void clearOptions() {
        for (JRadioButton option : options) {
            option.setSelected(false);
        }
    }

    private void finishQuiz() {
        int answeredQuestions = questionCounter - 1; // Subtract 1 to exclude the current question
        // perform actions such as displaying the score or closing the application
        JOptionPane.showMessageDialog(frame, "Quiz completed! You answered " + answeredQuestions + " questions.");
        System.exit(0); // Close the application
    }


    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new OnlineQuizApplication();
            }
        });
    }
}
