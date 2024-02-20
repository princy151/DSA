import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SocialMediaApp extends JFrame {
    private List<JPanel> posts;
    private JPanel postPanel;
    private JButton addPostButton;
    private Font postFont = new Font("Arial", Font.PLAIN, 14);
    private Color postBackgroundColor = new Color(240, 240, 240);
    private Color buttonBackgroundColor = new Color(150, 200, 255);
    private Color buttonHoverColor = new Color(100, 150, 255);

    private Graph socialGraph;
    private Map<String, UserProfile> userProfiles;

    public SocialMediaApp() {
        setTitle("Social Media App");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        posts = new ArrayList<>();
        postPanel = new JPanel();
        postPanel.setLayout(new BoxLayout(postPanel, BoxLayout.Y_AXIS));
        postPanel.setBackground(Color.WHITE);

        addPostButton = new JButton("Add Post");
        addPostButton.setBackground(buttonBackgroundColor);
        addPostButton.setForeground(Color.WHITE);
        addPostButton.setFocusPainted(false);
        addPostButton.setBorderPainted(false);
        addPostButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String postContent = JOptionPane.showInputDialog(SocialMediaApp.this, "Enter your post:");
                if (postContent != null && !postContent.isEmpty()) {
                    addNewPost(postContent);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(postPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(null);

        add(scrollPane, BorderLayout.CENTER);
        add(addPostButton, BorderLayout.SOUTH);

        socialGraph = new Graph();
        userProfiles = new HashMap<>();

        setVisible(true);
    }

    private void addNewPost(String postContent) {
        JPanel post = new JPanel();
        post.setLayout(new BorderLayout());
        post.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), new LineBorder(Color.LIGHT_GRAY)));
        post.setBackground(postBackgroundColor);

        JLabel postLabel = new JLabel("<html><body style='width: 300px;'>" + postContent + "</body></html>");
        postLabel.setFont(postFont);

        JButton likeButton = createButton("Like");
        JButton dislikeButton = createButton("Dislike");
        JButton commentButton = createButton("Comment");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(postBackgroundColor);
        buttonPanel.add(likeButton);
        buttonPanel.add(dislikeButton);
        buttonPanel.add(commentButton);

        post.add(postLabel, BorderLayout.NORTH);
        post.add(buttonPanel, BorderLayout.SOUTH);

        posts.add(post);
        postPanel.add(post);
        postPanel.revalidate();
        postPanel.repaint();

        String userName = "User";
        likeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(SocialMediaApp.this, "Liked post!");
                trackUserInteractions(userName, "like", postContent);
            }
        });

        dislikeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(SocialMediaApp.this, "Disliked post!");
                trackUserInteractions(userName, "dislike", postContent);
            }
        });

        commentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String comment = JOptionPane.showInputDialog(SocialMediaApp.this, "Enter your comment:");
                if (comment != null && !comment.isEmpty()) {
                    JOptionPane.showMessageDialog(SocialMediaApp.this, "Commented: " + comment);
                    trackUserInteractions(userName, "comment", postContent);
                }
            }
        });
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(buttonBackgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(buttonHoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(buttonBackgroundColor);
            }
        });
        return button;
    }

    private class Node {
        private String userName;

        public Node(String userName) {
            this.userName = userName;
        }

        public String getUserName() {
            return userName;
        }
    }

    private class Edge {
        private Node source;
        private Node destination;
        private String interactionType;

        public Edge(Node source, Node destination, String interactionType) {
            this.source = source;
            this.destination = destination;
            this.interactionType = interactionType;
        }

        public Node getSource() {
            return source;
        }

        public Node getDestination() {
            return destination;
        }

        public String getInteractionType() {
            return interactionType;
        }
    }

    private class Graph {
        private List<Node> nodes;
        private List<Edge> edges;

        public Graph() {
            nodes = new ArrayList<>();
            edges = new ArrayList<>();
        }

        public void addNode(Node node) {
            nodes.add(node);
        }

        public void addEdge(Node source, Node destination, String interactionType) {
            Edge edge = new Edge(source, destination, interactionType);
            edges.add(edge);
        }

        public List<Edge> getEdges() {
            return edges;
        }
    }

    private class UserProfile {
        private String userName;
        private List<String> interests;
        private List<String> connections;

        public UserProfile(String userName) {
            this.userName = userName;
            interests = new ArrayList<>();
            connections = new ArrayList<>();
        }

        public void addInterest(String interest) {
            interests.add(interest);
        }

        public void addConnection(String connection) {
            connections.add(connection);
        }

        public String getUserName() {
            return userName;
        }

        public List<String> getInterests() {
            return interests;
        }

        public List<String> getConnections() {
            return connections;
        }
    }

    private void trackUserInteractions(String userName, String interactionType, String targetContent) {
        String targetUserName = "Post Creator"; 
        socialGraph.addEdge(new Node(userName), new Node(targetUserName), interactionType);

        UserProfile userProfile = userProfiles.getOrDefault(userName, new UserProfile(userName));
        userProfile.addConnection(targetUserName);
        userProfiles.put(userName, userProfile);

        if (interactionType.equals("like")) {
            UserProfile targetProfile = userProfiles.getOrDefault(targetUserName, new UserProfile(targetUserName));
            targetProfile.addInterest("Liked posts");
            userProfiles.put(targetUserName, targetProfile);
        }
        List<String> recommendations = recommendContent(userName);
        displayRecommendedContentUI(recommendations);
    }

    private List<String> recommendContent(String userName) {
        List<String> recommendations = new ArrayList<>();
        UserProfile userProfile = userProfiles.get(userName);
        if (userProfile != null) {
            for (String connection : userProfile.connections) {
                UserProfile connectedProfile = userProfiles.get(connection);
                if (connectedProfile != null) {
                    recommendations.addAll(connectedProfile.interests);
                }
            }
        }
        return recommendations;
    }

    private void displayRecommendedContentUI(List<String> recommendations) {
        StringBuilder contentMessage = new StringBuilder("<html><body>");
        contentMessage.append("<h3 style='color: #333;'>Recommended Content</h3>");
        if (recommendations.isEmpty()) {
            contentMessage.append("<p>No recommendations available.</p>");
        } else {
            for (String recommendation : recommendations) {
                contentMessage.append("<div style='background-color: #f0f0f0; padding: 10px; margin-bottom: 10px;'>");
                contentMessage.append("<p>").append(recommendation).append("</p>");
                JButton likeButton = createButton("Like");
                JButton dislikeButton = createButton("Dislike");
                JButton commentButton = createButton("Comment");
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
                buttonPanel.setBackground(postBackgroundColor);
                buttonPanel.add(likeButton);
                buttonPanel.add(dislikeButton);
                buttonPanel.add(commentButton);
                contentMessage.append(buttonPanel);
                contentMessage.append("</div>");
            }
        }
        contentMessage.append("</body></html>");

        JLabel contentLabel = new JLabel(contentMessage.toString());
        JScrollPane scrollPane = new JScrollPane(contentLabel);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Recommended Content", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new SocialMediaApp();
            }
        });
    }
}