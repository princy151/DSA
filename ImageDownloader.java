import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ImageDownloader extends JFrame {

    private ExecutorService executorService;
    private static final String DOWNLOAD_DIRECTORY = "downloaded_files/";
    private List<Future<?>> downloadTasks;
    private Map<Future<?>, DownloadInfo> downloadInfoMap;
    private JTextField urlTextField;
    private JButton downloadButton;
    private JButton cancelButton;
    private JButton pauseButton;
    private JButton resumeButton;
    private JProgressBar progressBar;

    public ImageDownloader() {
        super("Image Downloader");
        initializeUI();
        executorService = Executors.newFixedThreadPool(5);
        downloadTasks = new CopyOnWriteArrayList<>();
        downloadInfoMap = new ConcurrentHashMap<>();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 200);
        setLocationRelativeTo(null); 

        JPanel mainPanel = new JPanel(new BorderLayout());


        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        urlTextField = new JTextField(30);
        urlTextField.setText("Enter Image URL(s)");
        topPanel.add(urlTextField);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        downloadButton = new JButton("Download");
        cancelButton = new JButton("Cancel");
        pauseButton = new JButton("Pause");
        resumeButton = new JButton("Resume");

        bottomPanel.add(downloadButton);
        bottomPanel.add(cancelButton);
        bottomPanel.add(pauseButton);
        bottomPanel.add(resumeButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(progressBar, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        downloadButton.addActionListener(e -> downloadbtnActionPerformed(e));
        cancelButton.addActionListener(e -> cancelbtnActionPerformed(e));
        pauseButton.addActionListener(e -> pausebtnActionPerformed(e));
        resumeButton.addActionListener(e -> resumebthActionPerformed(e));
    }

    private void downloadbtnActionPerformed(java.awt.event.ActionEvent evt) {
        String urlsText = urlTextField.getText();
        String[] urls = urlsText.split("[,\\s]+"); // Split the text by commas or whitespace
        for (String url : urls) {
            if (!url.isEmpty()) {
                downloadImage(url);
            }
        }
    }

    private void pausebtnActionPerformed(java.awt.event.ActionEvent evt) {
        pauseDownloads();
    }

    private void cancelbtnActionPerformed(java.awt.event.ActionEvent evt) {
        cancelDownloads();
    }

    private void resumebthActionPerformed(java.awt.event.ActionEvent evt) {
        resumeDownloads();
    }

    private void downloadImage(String urlString) {
        Runnable downloadTask = new Runnable() {
            @Override
            public void run() {
                DownloadInfo downloadInfo = downloadInfoMap.get(Thread.currentThread());
                int progress = downloadInfo != null ? downloadInfo.getProgress() : 0;
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                    if (progress > 0) {
                        connection.setRequestProperty("Range", "bytes=" + progress + "-");
                    }

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_PARTIAL) {
                        int contentLength = connection.getContentLength();
                        InputStream inputStream = connection.getInputStream();
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int bytesRead;

                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                            progress += bytesRead;
                            int currentProgress = (int) ((progress / (double) contentLength) * 100);
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setValue(currentProgress);
                                }
                            });

                            if (Thread.currentThread().isInterrupted()) {
                                throw new InterruptedException("Download interrupted");
                            }

                            Thread.sleep(50);
                        }

                        String fileName = "image_" + System.currentTimeMillis() + ".jpg";
                        saveImage(outputStream.toByteArray(), fileName);

                        inputStream.close();
                        outputStream.close();
                    } else {
                        throw new IOException("Failed to download image. Response code: " + responseCode);
                    }
                } catch (IOException | InterruptedException e) {
                    if (e instanceof InterruptedException) {
                        Thread.currentThread().interrupt();
                    }
                    if (!(e instanceof InterruptedException)) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Future<?> task = executorService.submit(downloadTask);
        downloadTasks.add(task);
        downloadInfoMap.put(task, new DownloadInfo(urlString, 0));
    }

    private void saveImage(byte[] imageData, String fileName) {
        File directory = new File(DOWNLOAD_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fullPath = DOWNLOAD_DIRECTORY + fileName;

        try {
            FileOutputStream outputStream = new FileOutputStream(fullPath);
            outputStream.write(imageData);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resumeDownloads() {
        for (Future<?> task : downloadTasks) {
            if (task.isCancelled()) {
                DownloadInfo downloadInfo = downloadInfoMap.get(task);
                if (downloadInfo != null) {
                    downloadImage(downloadInfo.getUrl());
                }
            }
        }
    }

    private void pauseDownloads() {
        for (Future<?> task : downloadTasks) {
            if (!task.isDone() && !task.isCancelled()) {
                task.cancel(true);
            }
        }
    }

    private void cancelDownloads() {
        for (Future<?> task : downloadTasks) {
            task.cancel(true);
        }
        progressBar.setValue(0);
    }

    private class DownloadInfo {
        private String url;
        private int progress;

        public DownloadInfo(String url, int progress) {
            this.url = url;
            this.progress = progress;
        }

        public String getUrl() {
            return url;
        }

        public int getProgress() {
            return progress;
        }
    }

    public static void main(String args[]) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ImageDownloader().setVisible(true);
            }
        });
    }
}