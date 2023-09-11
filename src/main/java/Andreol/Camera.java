package Andreol;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {
    private static final float gridTileWidth = 32.0f;
    private static final float gridTileHeight = 32.0f;
    private static final float tileCountX = 40.0f;
    private static final float tileCountY = 21.0f;
    private static final float clippingPlaneNear = 0.0f;
    private static final float clippingPlaneFar = 100.0f;

    private Matrix4f projectionMatrix, viewMatrix;
    public Vector2f position;

    public Camera(Vector2f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        adjustProjection();
    }

    public void adjustProjection() {
        this.projectionMatrix.identity();
        this.projectionMatrix.setOrtho(
                0.0f, gridTileWidth * tileCountX,
                0.0f, gridTileHeight * tileCountY,
                clippingPlaneNear, clippingPlaneFar);
    }

    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(position.x, position.y, 0.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        this.viewMatrix.identity();
        this.viewMatrix.setLookAt(
                new Vector3f(position.x, position.y, 20.0f),
                cameraFront,
                cameraUp);

        return this.viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }
}
