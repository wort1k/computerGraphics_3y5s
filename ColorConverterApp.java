import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class ColorConverterApp extends JFrame {
    private JTabbedPane tabbedPane;
    private JPanel colorDisplayPanel;

    // RGB components
    private JSpinner rSpinner, gSpinner, bSpinner;
    private JSlider rSlider, gSlider, bSlider;

    // HLS components
    private JSpinner hSpinner, lSpinner, sSpinner;
    private JSlider hSlider, lSlider, sSlider;

    // CMYK components
    private JSpinner cSpinner, mSpinner, ySpinner, kSpinner;
    private JSlider cSlider, mSlider, ySlider, kSlider;

    public ColorConverterApp() {
        setTitle("Color Picker");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();

        colorDisplayPanel = new JPanel();
        colorDisplayPanel.setPreferredSize(new Dimension(500, 200));
        add(colorDisplayPanel, BorderLayout.NORTH);

        tabbedPane.addTab("RGB", createRGBPanel());
        tabbedPane.addTab("HLS", createHLSPanel());
        tabbedPane.addTab("CMYK", createCMYKPanel());

        tabbedPane.addChangeListener(e -> updateColorOnTabChange());

        add(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel createRGBPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 3));

        panel.add(new JLabel("R:"));
        rSpinner = createSpinner(0, 255, 255);
        rSlider = createSlider(0, 255, 255);
        linkSpinnerAndSlider(rSpinner, rSlider);
        panel.add(rSpinner);
        panel.add(rSlider);

        panel.add(new JLabel("G:"));
        gSpinner = createSpinner(0, 255, 255);
        gSlider = createSlider(0, 255, 255);
        linkSpinnerAndSlider(gSpinner, gSlider);
        panel.add(gSpinner);
        panel.add(gSlider);

        panel.add(new JLabel("B:"));
        bSpinner = createSpinner(0, 255, 255);
        bSlider = createSlider(0, 255, 255);
        linkSpinnerAndSlider(bSpinner, bSlider);
        panel.add(bSpinner);
        panel.add(bSlider);

        JButton updateButton = new JButton("Update Color");
        updateButton.addActionListener(e -> updateColorFromRGB());
        panel.add(updateButton);

        return panel;
    }

    private JPanel createHLSPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 3));

        panel.add(new JLabel("H:"));
        hSpinner = createSpinner(0, 360, 360);
        hSlider = createSlider(0, 360, 360);
        linkSpinnerAndSlider(hSpinner, hSlider);
        panel.add(hSpinner);
        panel.add(hSlider);

        panel.add(new JLabel("L:"));
        lSpinner = createSpinner(0, 100, 50);
        lSlider = createSlider(0, 100, 50);
        linkSpinnerAndSlider(lSpinner, lSlider);
        panel.add(lSpinner);
        panel.add(lSlider);

        panel.add(new JLabel("S:"));
        sSpinner = createSpinner(0, 100, 100);
        sSlider = createSlider(0, 100, 100);
        linkSpinnerAndSlider(sSpinner, sSlider);
        panel.add(sSpinner);
        panel.add(sSlider);

        JButton updateButton = new JButton("Update Color");
        updateButton.addActionListener(e -> updateColorFromHLS());
        panel.add(updateButton);

        return panel;
    }

    private JPanel createCMYKPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 3));

        panel.add(new JLabel("C:"));
        cSpinner = createSpinner(0, 100, 0);
        cSlider = createSlider(0, 100, 0);
        linkSpinnerAndSlider(cSpinner, cSlider);
        panel.add(cSpinner);
        panel.add(cSlider);

        panel.add(new JLabel("M:"));
        mSpinner = createSpinner(0, 100, 0);
        mSlider = createSlider(0, 100, 0);
        linkSpinnerAndSlider(mSpinner, mSlider);
        panel.add(mSpinner);
        panel.add(mSlider);

        panel.add(new JLabel("Y:"));
        ySpinner = createSpinner(0, 100, 0);
        ySlider = createSlider(0, 100, 0);
        linkSpinnerAndSlider(ySpinner, ySlider);
        panel.add(ySpinner);
        panel.add(ySlider);

        panel.add(new JLabel("K:"));
        kSpinner = createSpinner(0, 100, 0);
        kSlider = createSlider(0, 100, 0);
        linkSpinnerAndSlider(kSpinner, kSlider);
        panel.add(kSpinner);
        panel.add(kSlider);

        JButton updateButton = new JButton("Update Color");
        updateButton.addActionListener(e -> updateColorFromCMYK());
        panel.add(updateButton);

        return panel;
    }

    private JSpinner createSpinner(int min, int max, int initial) {
        SpinnerNumberModel model = new SpinnerNumberModel(initial, min, max, 1);
        return new JSpinner(model);
    }

    private JSlider createSlider(int min, int max, int initial) {
        JSlider slider = new JSlider(min, max, initial);
        slider.setMajorTickSpacing(max / 5);
        slider.setPaintTicks(true);
        return slider;
    }

    private void linkSpinnerAndSlider(JSpinner spinner, JSlider slider) {
        spinner.addChangeListener(e -> {
            slider.setValue((int) spinner.getValue());
            updateColorFromCurrentTab(); // Update color when spinner changes
        });
        slider.addChangeListener(e -> {
            spinner.setValue(slider.getValue());
            updateColorFromCurrentTab(); // Update color when slider changes
        });
    }

    private void updateColorFromCurrentTab() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex == 0) {
            updateColorFromRGB();
        } else if (selectedIndex == 1) {
            updateColorFromHLS();
        } else if (selectedIndex == 2) {
            updateColorFromCMYK();
        }
    }

    private void updateColorFromRGB() {
        int r = (int) rSpinner.getValue();
        int g = (int) gSpinner.getValue();
        int b = (int) bSpinner.getValue();
        Color color = new Color(r, g, b);
        updateColor(color);
        updateCMYK(color);
        updateHLS(color);
    }

    private void updateColorFromHLS() {
        float h = (int) hSpinner.getValue();
        float l = (int) lSpinner.getValue() / 100f;
        float s = (int) sSpinner.getValue() / 100f;
        int[] rgb = Converter.HLS_to_RGB(h, l, s);
        Color color = new Color(rgb[0], rgb[1], rgb[2]);
        updateColor(color);
        updateCMYK(color);
        updateRGB(color);
    }

    private void updateColorFromCMYK() {
        float c = (int) cSpinner.getValue() / 100f;
        float m = (int) mSpinner.getValue() / 100f;
        float y = (int) ySpinner.getValue() / 100f;
        float k = (int) kSpinner.getValue() / 100f;
        int r = (int) ((1 - c) * (1 - k) * 255);
        int g = (int) ((1 - m) * (1 - k) * 255);
        int b = (int) ((1 - y) * (1 - k) * 255);
        Color color = new Color(r, g, b);
        updateColor(color);
        updateHLS(color);
        updateRGB(color);
    }

    private void updateColor(Color color) {
        colorDisplayPanel.setBackground(color);
    }

    private void updateCMYK(Color color) {
        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float k = 1 - Math.max(r, Math.max(g, b));
        float c = (1 - r - k) / (1 - k);
        float m = (1 - g - k) / (1 - k);
        float y = (1 - b - k) / (1 - k);
        cSpinner.setValue(Math.round(c * 100));
        mSpinner.setValue(Math.round(m * 100));
        ySpinner.setValue(Math.round(y * 100));
        kSpinner.setValue(Math.round(k * 100));
    }

    private void updateHLS(Color color) {
        float[] hls = Converter.RGB_to_HLS(color.getRed(), color.getGreen(), color.getBlue());
        hSpinner.setValue(Math.round(hls[0]));
        lSpinner.setValue(Math.round(hls[1] * 100));
        sSpinner.setValue(Math.round(hls[2] * 100));
    }

    private void updateRGB(Color color) {
        rSpinner.setValue(color.getRed());
        gSpinner.setValue(color.getGreen());
        bSpinner.setValue(color.getBlue());
    }

    private void updateColorOnTabChange() {
        updateColorFromCurrentTab(); // Update color display based on the selected tab
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ColorConverterApp::new);
    }
}
