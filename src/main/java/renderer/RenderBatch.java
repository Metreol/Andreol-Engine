package renderer;

import components.SpriteRenderer;

public class RenderBatch {
    /*
    Vertex ===========
    Pos (x, y)      Color (rgba)
    float, float,   float, float, float, float
    */
    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = (POS_OFFSET + POS_SIZE) * Float.BYTES;
    private final int VERTEX_SIZE = POS_SIZE + COLOR_SIZE;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private SpriteRenderer[] sprites;
    private int numSprites;
    private int maxBatchSize;
    private boolean hasRoom;
    private float[] vertices;
    private int vaoID, vboID, eboID;
    private Shader shader;

    public RenderBatch(int maxBatchSize) {
        shader = new Shader("assets/shaders/default.glsl");
        shader.compileAndLink();
        this.maxBatchSize = maxBatchSize;
        this.sprites = new SpriteRenderer[maxBatchSize];

        // 4 
    }

    public void start() {

    }

    public void addSprite(SpriteRenderer spriteRenderer) {

    }

    public int[] generateIndices() {

        return new int[1];
    }

    public void render() {

    }

    public void loadVertexProperties(int index) {

    }
}
