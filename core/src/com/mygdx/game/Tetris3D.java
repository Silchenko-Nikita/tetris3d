package com.mygdx.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.tetromino.Tetromino;
import com.mygdx.game.tetromino.TetrominoFactory;
import com.mygdx.game.utils.Coordinates;

import java.util.Objects;

import sun.jvm.hotspot.gc.shared.Space;


public class Tetris3D implements ApplicationListener {
	int screenX = 1800, screenY = 1000;
	float angle = 45.0f;
	float radius = 10.f;

	float timeSinceLastFall = 0.0f;

	public Field field = new Field();

	Tetromino activeTetromino;

	Coordinates activeTetrominoCoords = new Coordinates();
	Coordinates bufferedTetrominoCoords = new Coordinates();

	public Environment environment;
	public TetrominoFactory tetrominoFactory;
	public Camera cam;
	public CameraInputController camController;
	public ModelBatch modelBatch;
	public Model model;
	public Model bottomGridModel, backGridModel, frontGridModel, leftGridModel, rightGridModel;
	public ModelInstance tetrominoInstance;
	public ModelInstance bottomGridInstance, frontGridInstance, backGridInstance, leftGridInstance, rightGridInstance;

	@Override
	public void create() {
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		modelBatch = new ModelBatch();

		//cam = new OrthographicCamera(screenX / 100 * field.scale, screenY / 100 * field.scale);
		cam = new PerspectiveCamera(45.f, screenX / 100 * field.scale, screenY / 100 * field.scale);
		positionCamera();
		cam.update();

		ModelBuilder modelBuilder = new ModelBuilder();
		tetrominoFactory = new TetrominoFactory();

		activeTetromino = tetrominoFactory.createRandomTetromino();
		tetrominoInstance = createTetrominoModel(activeTetromino);

		modelBuilder.begin();

		MeshPartBuilder builder = modelBuilder.part("line", GL20.GL_LINES,
				VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
				new Material());
		builder.setColor(Color.WHITE);

		for (int y = 0; y <= field.HEIGHT; y++) {
			builder.line(0, y, 0, field.WIDTH, y, 0);
		}

		for (int x = 0; x <= field.WIDTH; x++) {
			builder.line(x, 0, 0, x, field.HEIGHT, 0);
		}

		bottomGridModel = modelBuilder.end();
		bottomGridInstance = new ModelInstance(bottomGridModel);

		modelBuilder.begin();

		builder = modelBuilder.part("line", GL20.GL_LINES,
				VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
				new Material());
		builder.setColor(Color.WHITE);

		for (int x = 0; x <= field.WIDTH; x++) {
			builder.line(x, 0, 0, x, 0, field.ALTITUDE);
		}

		for (int z = 0; z <= field.ALTITUDE; z++) {
			builder.line(0, 0, z, field.WIDTH, 0, z);
		}


		leftGridModel = modelBuilder.end();
		leftGridInstance = new ModelInstance(leftGridModel);


		modelBuilder.begin();

		builder = modelBuilder.part("line", GL20.GL_LINES,
				VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
				new Material());
		builder.setColor(Color.WHITE);

		for (int y = 0; y <= field.HEIGHT; y++) {
			builder.line(0, y, 0, 0, y, field.ALTITUDE);
		}

		for (int z = 0; z <= field.ALTITUDE; z++) {
			builder.line(0, 0, z, 0, field.HEIGHT, z);
		}

		backGridModel = modelBuilder.end();
		backGridInstance = new ModelInstance(backGridModel);

		modelBuilder.begin();

		builder = modelBuilder.part("line", GL20.GL_LINES,
				VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
				new Material());
		builder.setColor(Color.WHITE);

		for (int x = 0; x <= field.WIDTH; x++) {
			builder.line(x, field.HEIGHT, 0, x, field.HEIGHT, field.ALTITUDE);
		}

		for (int z = 0; z <= field.ALTITUDE; z++) {
			builder.line(0, field.HEIGHT, z, field.WIDTH, field.HEIGHT, z);
		}

		rightGridModel = modelBuilder.end();
		rightGridInstance = new ModelInstance(rightGridModel);


		modelBuilder.begin();

		builder = modelBuilder.part("line", GL20.GL_LINES,
				VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
				new Material());
		builder.setColor(Color.WHITE);

		for (int y = 0; y <= field.HEIGHT; y++) {
			builder.line(field.WIDTH, y, 0, field.WIDTH, y, field.ALTITUDE);
		}

		for (int z = 0; z <= field.ALTITUDE; z++) {
			builder.line(field.WIDTH, 0, z, field.WIDTH, field.HEIGHT, z);
		}

		frontGridModel = modelBuilder.end();
		frontGridInstance = new ModelInstance(frontGridModel);

		bottomGridInstance.transform.translate(-field.WIDTH / 2, -field.HEIGHT / 2, -field.ALTITUDE / 2);
		leftGridInstance.transform.translate(-field.WIDTH / 2, -field.HEIGHT / 2, -field.ALTITUDE / 2);
		backGridInstance.transform.translate(-field.WIDTH / 2, -field.HEIGHT / 2, -field.ALTITUDE / 2);
		rightGridInstance.transform.translate(-field.WIDTH / 2, -field.HEIGHT / 2, -field.ALTITUDE / 2);
		frontGridInstance.transform.translate(-field.WIDTH / 2, -field.HEIGHT / 2, -field.ALTITUDE / 2);

		activeTetrominoCoords.x = field.WIDTH / 2 - activeTetromino.GRID_WIDTH / 2;
		activeTetrominoCoords.y = field.HEIGHT / 2 - activeTetromino.GRID_WIDTH / 2;
		activeTetrominoCoords.z = field.ALTITUDE;
	}

	@Override
	public void render() {
		tetrominoInstance.transform.setTranslation(activeTetrominoCoords.x -field.WIDTH / 2 + 0.5f,
				activeTetrominoCoords.y -field.HEIGHT / 2+ 0.5f, activeTetrominoCoords.z
				- field.ALTITUDE / 2 - activeTetromino.GRID_WIDTH / 2 - 0.5f
		);

		handleInput();

		cam.update();

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		float deltaTime = Gdx.graphics.getDeltaTime();
		timeSinceLastFall += deltaTime;

		AKeyTimeSinceLastHandle += deltaTime;
		DKeyTimeSinceLastHandle += deltaTime;
		WKeyTimeSinceLastHandle += deltaTime;
		SKeyTimeSinceLastHandle += deltaTime;
		JKeyTimeSinceLastHandle += deltaTime;
		LKeyTimeSinceLastHandle += deltaTime;
		IKeyTimeSinceLastHandle += deltaTime;
		KKeyTimeSinceLastHandle += deltaTime;
		SpaceKeyTimeSinceLastHandle += deltaTime;

		if (timeSinceLastFall >= 3f) {
			if (!activeTetromino.fall(field, activeTetrominoCoords)) {
				field.addTetromino(activeTetromino, activeTetrominoCoords.x,
						activeTetrominoCoords.y, activeTetrominoCoords.z);

				activeTetrominoCoords.x = field.WIDTH / 2 - activeTetromino.GRID_WIDTH / 2;
				activeTetrominoCoords.y = field.HEIGHT / 2 - activeTetromino.GRID_WIDTH / 2;
				activeTetrominoCoords.z = field.ALTITUDE;

				activeTetromino = tetrominoFactory.createRandomTetromino();
				tetrominoInstance = createTetrominoModel(activeTetromino);

				tetrominoInstance.transform.setTranslation(activeTetrominoCoords.x -field.WIDTH / 2 + 0.5f,
						activeTetrominoCoords.y -field.HEIGHT / 2+ 0.5f, activeTetrominoCoords.z
								- field.ALTITUDE / 2 - activeTetromino.GRID_WIDTH / 2 - 0.5f
				);

				field.update();
			}
			timeSinceLastFall = 0;
		}

		modelBatch.begin(cam);
		modelBatch.render(tetrominoInstance, environment);

		displayScene();

		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();

		for (int i = 0; i < field.WIDTH; i++) {
			for (int j = 0; j < field.HEIGHT; j++) {
				for (int k = 0; k < field.ALTITUDE; k++) {
					if (field.grid[i][j][k] != Color.CLEAR) {
						createCube(modelBuilder, field.grid[i][j][k], i, j, k);
					}
				}
			}
		}

		Model model = modelBuilder.end();
		ModelInstance fallenTetrominosInst = new ModelInstance(model);

		fallenTetrominosInst.transform.setTranslation(-field.WIDTH / 2 + 0.5f, -field.HEIGHT / 2 + 0.5f, -field.ALTITUDE / 2 + 0.5f);

		modelBatch.render(fallenTetrominosInst, environment);
		modelBatch.end();
	}

	private void displayScene() {
		int intAngle = ((int) angle) % 360;

		if (intAngle < 0) {
			intAngle = 360 + intAngle;
		}

		if (intAngle >= 0 && intAngle < 180) {
			modelBatch.render(leftGridInstance, environment);
		}

		if (intAngle >= 90 && intAngle < 270) {
			modelBatch.render(frontGridInstance, environment);
		}

		if (intAngle >= 180 && intAngle < 360) {
			modelBatch.render(rightGridInstance, environment);
		}

		if (intAngle >= 270 || intAngle < 90) {
			modelBatch.render(backGridInstance, environment);
		}

		modelBatch.render(bottomGridInstance, environment);
	}

	private void positionCamera() {
		float x = 3f * radius * MathUtils.cosDeg(angle);
		float y = 3f * radius * MathUtils.sinDeg(angle);

		cam.position.set(x, y, 1.7f * radius);
		cam.up.set(0, 0, 1);
		cam.lookAt(0, 0, 0);
	}

	//
	float AKeyHandlingCooldown = 0.2f;
	float AKeyTimeSinceLastHandle = 0;

	float DKeyHandlingCooldown = 0.2f;
	float DKeyTimeSinceLastHandle = 0;

	float WKeyHandlingCooldown = 0.2f;
	float WKeyTimeSinceLastHandle = 0;

	float SKeyHandlingCooldown = 0.2f;
	float SKeyTimeSinceLastHandle = 0;

	float JKeyHandlingCooldown = 0.05f;
	float JKeyTimeSinceLastHandle = 0;

	float LKeyHandlingCooldown = 0.05f;
	float LKeyTimeSinceLastHandle = 0;

	float IKeyHandlingCooldown = 0.05f;
	float IKeyTimeSinceLastHandle = 0;

	float KKeyHandlingCooldown = 0.05f;
	float KKeyTimeSinceLastHandle = 0;

	float SpaceKeyTimeSinceLastHandle = 0.05f;
	float SpaceKeyHandlingCooldown = 0;
	//

	private void handleInput() {
		if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
			angle += 1f;

			positionCamera();
		}
		if (Gdx.input.isKeyPressed(Input.Keys.E)) {
			angle -= 1f;

			positionCamera();
		}

		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			if (AKeyTimeSinceLastHandle >= AKeyHandlingCooldown) {
				activeTetromino.rotateY_CW(field, activeTetrominoCoords);

				tetrominoInstance = createTetrominoModel(activeTetromino);
				tetrominoInstance.transform.setTranslation(activeTetrominoCoords.x -field.WIDTH / 2 + 0.5f,
						activeTetrominoCoords.y -field.HEIGHT / 2+ 0.5f, activeTetrominoCoords.z
								- field.ALTITUDE / 2 - activeTetromino.GRID_WIDTH / 2 - 0.5f
				);

				AKeyTimeSinceLastHandle = 0.f;
			}
		}
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			if (DKeyTimeSinceLastHandle >= DKeyHandlingCooldown) {
				activeTetromino.rotateY_CCW(field, activeTetrominoCoords);

				tetrominoInstance = createTetrominoModel(activeTetromino);
				tetrominoInstance.transform.setTranslation(activeTetrominoCoords.x -field.WIDTH / 2 + 0.5f,
						activeTetrominoCoords.y -field.HEIGHT / 2+ 0.5f, activeTetrominoCoords.z
								- field.ALTITUDE / 2 - activeTetromino.GRID_WIDTH / 2 - 0.5f
				);

				DKeyTimeSinceLastHandle = 0.f;
			}
		}
		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			if (WKeyTimeSinceLastHandle >= WKeyHandlingCooldown) {
				activeTetromino.rotateX_CW(field, activeTetrominoCoords);

				tetrominoInstance = createTetrominoModel(activeTetromino);
				tetrominoInstance.transform.setTranslation(activeTetrominoCoords.x -field.WIDTH / 2 + 0.5f,
						activeTetrominoCoords.y -field.HEIGHT / 2+ 0.5f, activeTetrominoCoords.z
								- field.ALTITUDE / 2 - activeTetromino.GRID_WIDTH / 2 - 0.5f
				);

				WKeyTimeSinceLastHandle = 0.f;
			}
		}
		if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			if (SKeyTimeSinceLastHandle >= SKeyHandlingCooldown) {
				activeTetromino.rotateX_CCW(field, activeTetrominoCoords);

				tetrominoInstance = createTetrominoModel(activeTetromino);
				tetrominoInstance.transform.setTranslation(activeTetrominoCoords.x -field.WIDTH / 2 + 0.5f,
						activeTetrominoCoords.y -field.HEIGHT / 2+ 0.5f, activeTetrominoCoords.z
								- field.ALTITUDE / 2 - activeTetromino.GRID_WIDTH / 2 - 0.5f
				);

				SKeyTimeSinceLastHandle = 0.f;
			}
		}

		if (Gdx.input.isKeyPressed(Input.Keys.J)) {
			if (JKeyTimeSinceLastHandle >= JKeyHandlingCooldown) {
				bufferedTetrominoCoords.x = 1;
				activeTetromino.move(field, activeTetrominoCoords, bufferedTetrominoCoords);
				bufferedTetrominoCoords.x = 0;

				JKeyTimeSinceLastHandle = 0.f;
			}
		}

		if (Gdx.input.isKeyPressed(Input.Keys.L)) {
			if (LKeyTimeSinceLastHandle >= LKeyHandlingCooldown) {
				bufferedTetrominoCoords.x = -1;
				activeTetromino.move(field, activeTetrominoCoords, bufferedTetrominoCoords);
				bufferedTetrominoCoords.x = 0;

				LKeyTimeSinceLastHandle = 0.f;
			}
		}

		if (Gdx.input.isKeyPressed(Input.Keys.I)) {
			if (IKeyTimeSinceLastHandle >= IKeyHandlingCooldown) {
				bufferedTetrominoCoords.y = -1;
				activeTetromino.move(field, activeTetrominoCoords, bufferedTetrominoCoords);
				bufferedTetrominoCoords.y = 0;

				IKeyTimeSinceLastHandle = 0.f;
			}
		}

		if (Gdx.input.isKeyPressed(Input.Keys.K)) {
			if (KKeyTimeSinceLastHandle >= KKeyHandlingCooldown) {
				bufferedTetrominoCoords.y = 1;
				activeTetromino.move(field, activeTetrominoCoords, bufferedTetrominoCoords);
				bufferedTetrominoCoords.y = 0;

				KKeyTimeSinceLastHandle = 0.f;
			}
		}

		if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
			if (SpaceKeyTimeSinceLastHandle >= SpaceKeyHandlingCooldown) {
				activeTetromino.fall(field, activeTetrominoCoords);

				SpaceKeyTimeSinceLastHandle = 0.f;
			}
		}
	}

	public ModelInstance createTetrominoModel(Tetromino tetromino) {
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();

		for (int x = 0; x < tetromino.GRID_WIDTH; x++) {
			for (int y = 0; y < tetromino.GRID_WIDTH; y++) {
				for (int z = 0; z < tetromino.GRID_WIDTH; z++) {
					if (tetromino.grid[x][y][z]) {
						createCube(modelBuilder, tetromino.color, x, y, z);
					}
				}
			}
		}

		Model model = modelBuilder.end();
		return new ModelInstance(model);
	}

	public void createCube(ModelBuilder modelBuilder, Color color, int x, int y, int z) {
		Material fillMaterial = new Material(ColorAttribute.createDiffuse(color));
		Material lineMaterial = new Material(ColorAttribute.createDiffuse(Color.WHITE));

		float cubeSize = 1f;

		float startX = (x - 0.5f) * cubeSize;
		float startY = (y - 0.5f) * cubeSize ;
		float startZ = (z - 0.5f) * cubeSize;

		MeshPartBuilder meshBuilder = modelBuilder.part("cube" + x + y + z, GL20.GL_TRIANGLES,
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
				fillMaterial);
		meshBuilder.box(x * cubeSize, y * cubeSize, z * cubeSize, cubeSize, cubeSize, cubeSize);

		MeshPartBuilder lineBuilder = modelBuilder.part("line" + x + y + z, GL20.GL_LINES,
				VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
				lineMaterial);

		// Draw lines around the cube
		for (float offset = 0; offset <= cubeSize; offset += cubeSize) {
			for (float i = 0; i <= cubeSize; i += cubeSize) {
				lineBuilder.line(startX + offset, startY + i, startZ, startX + offset, startY + i, startZ + cubeSize);
				lineBuilder.line(startX + i, startY + offset, startZ, startX + i, startY + offset, startZ + cubeSize);
				lineBuilder.line(startX + offset, startY, startZ + i, startX + offset, startY + cubeSize, startZ + i);
				lineBuilder.line(startX, startY + offset, startZ + i, startX + cubeSize, startY + offset, startZ + i);
			}
		}
	}

	@Override
	public void dispose() {
		modelBatch.dispose();
		model.dispose();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
