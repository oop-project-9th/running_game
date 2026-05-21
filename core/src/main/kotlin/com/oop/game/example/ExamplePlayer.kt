package com.oop.game.example

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.GameObject
import com.oop.game.InputHandler

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *  플레이어 예제 — player.png 이미지, 화살표 키로 조종.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *
 *  GameObject 를 상속하는 '가장 단순한' 예제다.
 *  자기 프로젝트의 Player 를 만들 때 이 파일을 통째로 복사해서
 *  texture 의 파일명을 자기 이미지로 바꾸거나,
 *  update() 에 발사 로직·특수 능력 등을 추가하면 된다.
 *
 *  핵심 포인트:
 *   ▸ Texture 는 객체가 살아있는 동안 한 번만 만들고 재사용 (생성 비용이 큼).
 *   ▸ 객체가 사라질 때 dispose() 로 GPU 자원 해제 — 기본 GameObject.dispose()를 override.
 *   ▸ batch.draw(texture, x, y, w, h) 한 줄로 이미지를 그린다.
 *
 * @param worldWidth/Height: 월드 크기를 받아 경계 밖으로 못 나가게 제한하는 용도.
 */
class ExamplePlayer(
    x: Float,
    y: Float,
    private val worldWidth: Float,
    private val worldHeight: Float,
    private val groundY: Float //땅 위치 추가
) : GameObject(x, y, 30f, 30f) {

    private enum class State {
        RUNNING, // 달리기
        JUMPING, // 점프 중
        SLIDING  // 슬라이드 중
    }

    // 이미지 로딩.
    //   Gdx.files.internal: 클래스패스(자원 폴더)에서 파일을 찾아 읽는다.
    //   Texture 는 GPU 메모리에 이미지를 올린 핸들이다.
    //   src/main/resources/player.png 에 위치.
    private val texture = Texture(Gdx.files.internal("player.png"))

    private var state = State.RUNNING

    private val speed = 200f
    private val jumpPower = 500f
    private val gravity = -1000f

    private var velocityY = 0f

    override fun update(delta: Float) {
        handleInput() // 키 입력에 따라 각 움직임 함수 호출
        moveForward(delta) // 프레임마다 지속적으로 우측으로 가도록 함
        updateState(delta) //달리기, 점프, 슬라이드 상태 변화 감지
        wholeWorld()
    }

    private fun handleInput() {
        if (state == State.RUNNING && InputHandler.isKeyJustPressed(InputHandler.SPACE)) {
            startJump()
        }
    }

    private fun updateState(delta: Float) {
        when (state) {
            State.RUNNING -> {
                // 달리는 상태이고 디폴트 상태임
            }

            State.JUMPING -> {
                updateJump(delta) //점프 함
            }

            State.SLIDING -> {
                // 슬라이드는 나중에 구현
            }
        }
    }

    private fun moveForward(delta: Float) {
        x += speed * delta //speed는 일단 200f로 고정
    }

    private fun startJump() {
        state = State.JUMPING
        velocityY = jumpPower
    }

    private fun updateJump(delta: Float) {
        velocityY += gravity * delta // 중력은 일단 -1000f
        y += velocityY * delta // y위치는 중력 반영해서 프레임마다 업데이트

        if (y <= groundY) { // y위치가 땅보다 밑이거나 같게 되면 y의 속도는 0f로 초기화 해야됨
            y = groundY
            velocityY = 0f
            state = State.RUNNING // y위치가 초기화 되면 땅에 붙어있다는 뜻이므로 달리기 상태가 되야함
        }
    }

    private fun wholeWorld() {
        x = x.coerceIn(0f, worldWidth - width)
        y = y.coerceIn(groundY, worldHeight - height)
    }
    /**
     * 매 프레임 호출 — 자신의 이미지를 그린다.
     *
     * batch.draw(texture, x, y, w, h):
     *   왼쪽 아래 (x, y) 지점부터 (w, h) 크기로 텍스처를 늘려서 그린다.
     *   원본 이미지가 30x30 이고 w=30, h=30 이면 1:1 그대로 그려진다.
     */
    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    /** GPU 자원 정리 — 화면이 닫힐 때 GameWorld 가 호출. */
    override fun dispose() {
        texture.dispose()
    }
}
