import static java.lang.Math.*;

public class Converter {

    public static double[] RGB_to_CMYK(double R, double G, double B) {
        double tmp = max(R, max(G, B));
        double K = 1 - tmp / 255;
        double C = (1 - R / 255 - K) / (1 - K);
        double M = (1 - G / 255 - K) / (1 - K);
        double Y = (1 - B / 255 - K) / (1 - K);
        return new double[]{C, M, Y, K};
    }

    public static double[] CMYK_to_RGB(double C, double M, double Y, double K) {
        double R = 255 * (1 - C) * (1 - K);
        double G = 255 * (1 - M) * (1 - K);
        double B = 255 * (1 - Y) * (1 - K);
        return new double[]{R, G, B};
    }

    public static int[] HLS_to_RGB(float H, float L, float S) {
        H = (H % 360) / 360.0f; // Привести H к диапазону [0, 1]

        if (S == 0) {
            int color = (int) (L * 255);
            return new int[]{color, color, color};
        }

        float tmp1 = L < 0.5 ? L * (1 + S) : L + S - L * S;
        float tmp2 = 2 * L - tmp1;

        float[] tmpColors = new float[]{
                (H + 1f / 3f) % 1,
                H,
                (H - 1f / 3f + 1) % 1
        };

        float[] RGB = new float[3];
        for (int i = 0; i < 3; i++) {
            if (6 * tmpColors[i] < 1) {
                RGB[i] = tmp2 + (tmp1 - tmp2) * 6 * tmpColors[i];
            } else if (2 * tmpColors[i] < 1) {
                RGB[i] = tmp1;
            } else if (3 * tmpColors[i] < 2) {
                RGB[i] = tmp2 + (tmp1 - tmp2) * (2f / 3f - tmpColors[i]) * 6;
            } else {
                RGB[i] = tmp2;
            }
        }

        return new int[]{
                (int) (RGB[0] * 255),
                (int) (RGB[1] * 255),
                (int) (RGB[2] * 255)
        };
    }

    public static float[] RGB_to_HLS(float R, float G, float B) {
        R /= 255;
        G /= 255;
        B /= 255;
        float min = min(R, min(G, B));
        float max = max(R, max(G, B));
        float delta = max - min;
        float L = (min + max) / 2;
        float H = 0, S;

        if (min == max) {
            return new float[]{0, L, 0};
        }

        S = delta / (1 - abs(2 * L - 1));

        if (R == max) {
            H = (G - B) / delta;
        } else if (G == max) {
            H = 2 + (B - R) / delta;
        } else if (B == max) {
            H = 4 + (R - G) / delta;
        }

        H *= 60;
        if (H < 0) {
            H += 360;
        }

        return new float[]{H, L, S};
    }
}
