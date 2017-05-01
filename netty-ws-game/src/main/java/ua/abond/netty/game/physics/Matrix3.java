package ua.abond.netty.game.physics;

public class Matrix3 {
    private final float[][] matrix = new float[3][3];

    public Matrix3() {
        toIdentity(this.matrix);
    }

    public Matrix3 scale(float x, float y) {
        matrix[0][0] *= x;
        matrix[1][1] *= y;
        return this;
    }

    public Matrix3 setScaleTo(float x, float y) {
        matrix[0][0] = x;
        matrix[1][1] = y;
        return this;
    }

    public Matrix3 translate(float x, float y) {
        matrix[2][0] += x;
        matrix[2][1] += y;
        return this;
    }

    public Matrix3 setPositionTo(float x, float y) {
        matrix[2][0] = x;
        matrix[2][1] = y;
        return this;
    }

    public Matrix3 rotate(float x, float y) {
        throw new UnsupportedOperationException();
    }

    public Matrix3 setRotationTo(float x, float y) {
        throw new UnsupportedOperationException();
    }

    private static float[][] toIdentity(float[][] matrix) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                matrix[i][j] = i == j ? 1 : 0;
            }
        }
        return matrix;
    }
}
