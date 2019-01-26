package br.com.philippesis.fun.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	private SpriteBatch batch;

	private Texture[] bird;
	private Texture background;
	private Texture canoBaixo;
	private Texture canoTopo;
    private Random numeroRandomico;
    private BitmapFont bitmapFont;

    // Atributos de configuração
    private int movimento = 0;
    private int larguraDispositivo;
    private int alturaDispositivo;

    private float variacao = 0;
    private float queda = 0;
    private float velocidadeQueda = 30;
    private float velocidadeMovimentoCano = 120;
    private float posicaoInicialVertical;
    private boolean primeiroToque = false;
    private float posicaoMovHorizCano;
    private float espacoEntreCanosTopo;
    private float espacoEntreCanosBaixo;
    private float deltaTime;
    private float alturaEntreCanosRandon;
    private int pontuacao = 0;
    private boolean pontuou = false;
    private int posicaoPassaro;
	
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

        //
        bitmapFont = new BitmapFont();
        bitmapFont.setColor(Color.WHITE);
        bitmapFont.getData().setScale(6);

		// Configurações
        larguraDispositivo = Gdx.graphics.getWidth();
        alturaDispositivo = Gdx.graphics.getHeight();
        posicaoInicialVertical = alturaDispositivo / 2;
        posicaoMovHorizCano = larguraDispositivo;
        espacoEntreCanosTopo = 150;
        espacoEntreCanosBaixo = 150;
        numeroRandomico = new Random();
        posicaoPassaro = larguraDispositivo / 3;

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		deltaTime = Gdx.graphics.getDeltaTime();

		// Variação para bater das asas (troca das imagens)
		variacao += deltaTime * 8;
		if(variacao > 4) {
		    variacao = 0;
        }

        // Iniciar jogo
        if(!primeiroToque) {
            if(Gdx.input.justTouched()) {
                primeiroToque = true;
                queda = -12;
            }
        } else {
            // Taxa de subida ao toque
            if (Gdx.input.justTouched() && (posicaoInicialVertical < (alturaDispositivo - 200))) {
                queda = -12;
            }
            // Taxa queda
            if (posicaoInicialVertical >= 10) {
                queda += deltaTime * velocidadeQueda;
                posicaoInicialVertical -= queda;
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
                    pontuacao++;
                    velocidadeMovimentoCano += 10;
                }
                pontuou = true;
            }
        }

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
        bitmapFont.draw(batch, String.valueOf(pontuacao), larguraDispositivo / 2 - 50, alturaDispositivo - 100);
		batch.end();
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

}
