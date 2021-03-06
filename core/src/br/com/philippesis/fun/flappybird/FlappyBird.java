package br.com.philippesis.fun.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

	private SpriteBatch batch;
	private Texture[] bird;
	private Texture background;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameOver;

    private Random numeroRandomico;
    private BitmapFont pontuacaoBitmap;
    private BitmapFont mensagemBitmap;
    private BitmapFont creator;
    private Circle passaroCirculo;
    private Rectangle canoTopoRetangulo;
    private Rectangle canoBaixoRetangulo;

    // Atributos de configuração
    private float larguraDispositivo;
    private float alturaDispositivo;

    private float variacao = 0;
    private float queda = 0;
    private float velocidadeMovimentoCano = 120;
    private float posicaoInicialVertical;
    private int estadoJogo = 0; //0 Não iniciou, 1 Iniciou, 2 Game Over
    private float posicaoMovHorizCano;
    private float espacoEntreCanosTopo;
    private float espacoEntreCanosBaixo;
    private float alturaEntreCanosRandon;
    private int pontuacao = 0;
    private boolean pontuou = false;
    private float posicaoPassaro;

    // Câmera
    private OrthographicCamera camera;
    private Viewport viewport;


    @Override
	public void create () {
		batch = new SpriteBatch();
        // Textura passaro
		bird = new Texture[5];
		bird[0] = new Texture("passaro1.png");
        bird[1] = new Texture("passaro2.png");
        bird[2] = new Texture("passaro3.png");
        bird[3] = new Texture("passaro2.png");
        bird[4] = new Texture("passaro1.png");
        // Textura background
		background = new Texture("fundo.png");
		// Textura canos
        canoBaixo = new Texture("cano_baixo_maior.png");
        canoTopo = new Texture("cano_topo_maior.png");

        gameOver = new Texture("game_over.png");

        // Bitmap pontuação
        pontuacaoBitmap = new BitmapFont();
        pontuacaoBitmap.setColor(Color.WHITE);
        pontuacaoBitmap.getData().setScale(6);

        // Bitmap msgReinicio jogo
        mensagemBitmap = new BitmapFont();
        mensagemBitmap.setColor(Color.WHITE);
        mensagemBitmap.getData().setScale(3);

        // Creator
        creator = new BitmapFont();
        creator.setColor(Color.WHITE);
        creator.getData().setScale(2);

		// Configurações
        float VIRTUAL_WIDTH = 768;
        larguraDispositivo = VIRTUAL_WIDTH;//Gdx.graphics.getWidth();
        float VIRTUAL_HEIGHT = 1024;
        alturaDispositivo = VIRTUAL_HEIGHT;//Gdx.graphics.getHeight();
        posicaoInicialVertical = alturaDispositivo / 2;
        posicaoMovHorizCano = larguraDispositivo;
        espacoEntreCanosTopo = 150;
        espacoEntreCanosBaixo = 150;
        numeroRandomico = new Random();
        posicaoPassaro = larguraDispositivo / 3;

        // Colisões
        passaroCirculo = new Circle();
        canoTopoRetangulo = new Rectangle();
        canoBaixoRetangulo = new Rectangle();

        // Tela
        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);

        // Preenche a tela, pode esticar ou deformar as imagens.
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        // Ajusta tamanho da tela
        //viewport = new FillViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float deltaTime = Gdx.graphics.getDeltaTime();

		// Variação para bater das asas (troca das imagens)
		variacao += deltaTime * 8;
		if(variacao > 4) {
		    variacao = 0;
        }

        // Iniciar jogo
        if(estadoJogo == 0) { // Não iniciado
            if(Gdx.input.justTouched()) {
                estadoJogo = 1;
                queda = -12;
            }
        } else if(estadoJogo == 1) { // Iniciado
            // Taxa de subida ao toque
            if (Gdx.input.justTouched() && (posicaoInicialVertical < (alturaDispositivo - 200))) {
                queda = -12;
            }
            // Taxa queda
            if (posicaoInicialVertical >= 10) {
                float velocidadeQueda = 30;
                queda += deltaTime * velocidadeQueda;
                posicaoInicialVertical -= queda;
            } else {
                estadoJogo = 2;
            }
            // Movimento canos
            if(posicaoMovHorizCano >= -100) {
                posicaoMovHorizCano -= deltaTime * velocidadeMovimentoCano;
            } else {
                // Reinicia cano
                posicaoMovHorizCano = larguraDispositivo;
                // Altera altura cano
                alturaEntreCanosRandon = numeroRandomico.nextInt(500) - 200;
                //
                pontuou = false;
            }
            // Gerar pontuacao
            if(posicaoMovHorizCano < posicaoPassaro - 100) {
                if(!pontuou) {

                    // Incrementa pontuação
                    pontuacao++;

                    // Almenta velocidade conforme pontuação
                    if(pontuacao <= 10) {
                        velocidadeMovimentoCano += 12;
                    } else if(pontuacao <= 20) {
                        velocidadeMovimentoCano += 10;
                    } else if(pontuacao <= 30) {
                        velocidadeMovimentoCano += 8;
                    } else if(pontuacao <= 40) {
                        velocidadeMovimentoCano += 6;
                    } else if(pontuacao <= 50) {
                        velocidadeMovimentoCano += 4;
                    } else if(pontuacao <= 60) {
                        velocidadeMovimentoCano += 2;
                    } else {
                        velocidadeMovimentoCano++;
                    }
                }
                pontuou = true;
            }
        } else { // Pausado
            if(Gdx.input.justTouched()) {
                estadoJogo = 0;
                pontuacao = 0;
                velocidadeMovimentoCano = 120;
                posicaoInicialVertical = alturaDispositivo / 2;
                posicaoMovHorizCano = larguraDispositivo;
            }
        }

        // Configurar dados de projeção da câmera
        batch.setProjectionMatrix(camera.combined);

        // Desenhar
		batch.begin();

        // Background
		batch.draw(background, 0, 0, larguraDispositivo, alturaDispositivo);
		// Passaro
		batch.draw(bird[(int) variacao], posicaoPassaro, posicaoInicialVertical);
		// Cano Topo
        batch.draw(canoTopo, posicaoMovHorizCano, canoCimaY());
        // Cano Baixo
        batch.draw(canoBaixo, posicaoMovHorizCano, canoBaixoY());
        // Exibir pontuação
        pontuacaoBitmap.draw(batch, String.valueOf(pontuacao), larguraDispositivo / 2 - 50,
                alturaDispositivo - 100);

        // Game Over
        if(estadoJogo == 2) {
            // Imagem Game Over
            batch.draw(gameOver, (larguraDispositivo / 2) - (gameOver.getWidth() / 2), alturaDispositivo / 2);
            // Mensagem para reiniciar
            mensagemBitmap.draw(batch, "Toque Para Reiniciar. :-)", (larguraDispositivo / 2) -
                    (gameOver.getWidth() / 2) - 34, (alturaDispositivo / 2) - gameOver.getHeight() / 3);
            creator.draw(batch, "Cloned for study by Roger Philippe - 2019", 10, 30);
        }

		batch.end();

		// Colisões
		// Passaro (forma para colisão)
		passaroCirculo.set(posicaoPassaro+30, posicaoInicialVertical+24, 28);
		// Cano Baixo (forma para colisão)
		canoBaixoRetangulo = new Rectangle(posicaoMovHorizCano, canoBaixoY(), canoBaixo.getWidth(),
                canoBaixo.getHeight());
		// Cano Topo (forma colisão)
        canoTopoRetangulo = new Rectangle(posicaoMovHorizCano, canoCimaY(), canoTopo.getWidth(),
                canoTopo.getHeight());

        // Verifica colisão
        if(Intersector.overlaps(passaroCirculo, canoTopoRetangulo) || Intersector
                .overlaps(passaroCirculo, canoBaixoRetangulo)) {
            estadoJogo = 2;
        }

	}

	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}

	private float canoCimaY() {

	    float canoCimaY;

	    // Pega altura do cano
	    canoCimaY = (alturaDispositivo / 2) + espacoEntreCanosTopo + alturaEntreCanosRandon;

	    return canoCimaY;
    }

    private float canoBaixoY() {

        float canoBaixoY;

        // Pega altura do cano
        canoBaixoY = ((alturaDispositivo / 2) - canoBaixo.getHeight()) - espacoEntreCanosBaixo + alturaEntreCanosRandon;

	    return canoBaixoY;
    }

    @Override
    public void resize(int width, int height) {
	    viewport.update(width, height);
    }

}
