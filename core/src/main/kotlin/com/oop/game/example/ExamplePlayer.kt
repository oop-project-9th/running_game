package com.oop.game.example

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.GameObject
import com.oop.game.InputHandler

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 플레이어 예제 — player.png 이미지, 화살표 키로 조종.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *
 * GameObject 를 상속하는 '가장 단순한' 예제다.
 * 자기 프로젝트의 Player 를 만들 때 이 파일을 통째로 복사해서
 * texture 의 파일명을 자기 이미지로 바꾸거나,
 * update() 에 발사 로직·특수 능력 등을 추가하면 된다.
 *
 * 핵심 포인트:
 * ▸ Texture 는 객체가 살아있는 동안 한 번만 만들고 재사용 (생성 비용이 큼).
 * ▸ 객체가 사라질 때 dispose() 로 GPU 자원 해제 — 기본 GameObject.dispose()를 override.
 * ▸ batch.draw(texture, x, y, w, h) 한 줄로 이미지를 그린다.
 *
 * @param worldWidth/Height: 월드 크기를 받아 경계 밖으로 못 나가게 제한하는 용도.
 */
class ExamplePlayer(
    x: Float,
    y: Float,
    private val worldWidth: Float,
    private val worldHeight: Float,
    private val groundY: Float //땅 위치 추가
) : GameObject(x, y, 150f, 150f) {
    private enum class State {
        RUNNING, // 달리기
        JUMPING, // 점프 중
        SLIDING  // 슬라이드 중
    }

    // 이미지 로딩.
    //   Gdx.files.internal: 클래스패스(자원 폴더)에서 파일을 찾아 읽는다.
    //   Texture 는 GPU 메모리에 이미지를 올린 핸들이다.
    //   src/main/resources/player.png 에 위치.
    private val runTexture = Texture(Gdx.files.internal("player.png"))
    private val slideTexture = Texture(Gdx.files.internal("slide.png"))

    private var state = State.RUNNING
    private val baseJumpCount = 2 // 기본은 2단 점프
    private var bonusJumpCount = 0
    private var jumpCount = 0
    private val defaultWidth = 150f
    private val defaultHeight = 150f //달리고 있을때 기본 높이 이고 슬라이드시, 절반으로 줄어들어야함
    private val slideWidth = 135f
    private val slideHeight = 90f

    private val speed = 200f
    private val jumpPower = 1200f // 쿠키런 조작감 구현
    private val gravity = -4000f
    private var velocityY = 0f

    // ── [작성자: 본인 이름] HP 및 무적 시스템 변수 추가 ──
    private var hp = 3
    private var invincibleTimer = 0f
    private val invincibleTime = 1f

    override fun update(delta: Float) {
        // ── [작성자: 본인 이름] 무적 시간 감소 로직 추가 ──
        if (invincibleTimer > 0f) {
            invincibleTimer -= delta
        }

        handleInput() // 키 입력에 따라 각 움직임 함수 호출
        moveForward(delta) // 프레임마다 지속적으로 우측으로 가도록 함
        updateJump(delta) //달리기, 점프, 슬라이드 상태 변화 감지
        wholeWorld()
    }

    private fun handleInput() {
        if (updateSlide()) return //슬라이드가 인풋 되어 있는 상태에서는 점프 못하게 리턴

        if (InputHandler.isKeyJustPressed(InputHandler.SPACE) && //얘네도 통일성을 위해 함수로 바꿀 예정
            jumpCount < getMaxJumpCount()) // 아이템 효과로 3단 점프 이상도 가능
        {
            startJump()
        }
    }

    private fun moveForward(delta: Float) {
        x += speed * delta //speed는 일단 200f로 고정
    }

    private fun isOnGround(): Boolean { // 땅에 닿아 있는지를 지속적으로 확인해야됨
        return y <= groundY
    }

    private fun getMaxJumpCount(): Int {
        return baseJumpCount + bonusJumpCount
    }

    private fun startJump() {
        state = State.JUMPING
        velocityY = jumpPower
        jumpCount++ // 1단 점프시 0->1로 변경, 2단 점프시 1->2로 변경
    }

    private fun updateJump(delta: Float) {
        if (state != State.JUMPING) return // 점프 상황 아닐때 중력 적용 x

        velocityY += gravity * delta
        y += velocityY * delta // y위치는 중력 반영해서 프레임마다 업데이트

        if (isOnGround()) { // 캐릭터가 땅에 닿으면 y를 땅으로 고정하고 y가속도 0설정
            y = groundY
            velocityY = 0f
            jumpCount = 0 // 땅에 닿은 순간이므로 2단 점프 가능 상태로 변경
            state = State.RUNNING // y위치가 초기화 되면 땅에 붙어있다는 뜻이므로 달리기 상태가 되야함
        }
    }

    private fun updateSlide(): Boolean {
        val slideState = InputHandler.isKeyPressed(InputHandler.Z) && isOnGround()
        //z 누르고 있는 여부로 슬라이드 스테이트 조정
        if (slideState) { //z키를 누르고 있는데 슬라이드 중이 아니면 슬라이드 상태로 전환
            if (state != State.SLIDING) {
                startSlide()
            }
            return true //z키를 누르고 있는 상태이므로 리턴해서 상태 유지
        }

        if (state == State.SLIDING) { //z키를 누르고 있는 중이 아니므로 슬라이드 상태 취소
            stopSlide()
        }

        return false
    }

    private fun startSlide() { // 슬라이드 시작 상태 슬라이드 하는 높이 만큼 캐릭터 변경
        state = State.SLIDING
        width = slideWidth
        height = slideHeight
        y = groundY
    }

    private fun stopSlide() { // 슬라이드 끝 디폴트 상태(달리기) 높이만큼 캐릭터 변경
        width = defaultWidth
        height = defaultHeight
        y = groundY
        state = State.RUNNING
    }

    private fun wholeWorld() {
        x = x.coerceAtLeast(0f)//gpt 도움 2...
        y = y.coerceIn(groundY, worldHeight - height)
    }

    fun setBonusJumpCount(value: Int) {
        bonusJumpCount = value.coerceIn(0, 3)
    }

    fun isAirborne(): Boolean {
        return !isOnGround()
    }

    fun isGrounded(): Boolean {
        return isOnGround()
    }

    fun isSliding(): Boolean {
        return state == State.SLIDING
    }

    fun launchUp(power: Float) {
        if (state == State.SLIDING) {
            stopSlide()
        }

        state = State.JUMPING
        velocityY = power
        jumpCount = 1
    }

    fun forceLanding() {
        y = groundY
        velocityY = 0f
        jumpCount = 0
        state = State.RUNNING
        stopSlide()
    }

    fun dashForward(distance: Float) {
        x += distance
    }

    /**
     * 매 프레임 호출 — 자신의 이미지를 그린다.
     *
     * batch.draw(texture, x, y, w, h):
     * 왼쪽 아래 (x, y) 지점부터 (w, h) 크기로 텍스처를 늘려서 그린다.
     * 원본 이미지가 30x30 이고 w=30, h=30 이면 1:1 그대로 그려진다.
     */
    override fun draw(batch: SpriteBatch) {
        val currentTexture = if (state == State.SLIDING) {
            slideTexture
        } else {
            runTexture
        }

        batch.draw(currentTexture, x, y, width, height)
    }

    // ── [작성자: 본인 이름] HP 및 데미지 함수 추가 ──
    fun getHp(): Int {
        return hp
    }

    fun takeDamage(damage: Int) {
        if (invincibleTimer <= 0f) {
            hp -= damage
            if (hp < 0) {
                hp = 0
            }
            invincibleTimer = invincibleTime // 피격 시 1초 무적 발동
        }
    }

    fun isDead(): Boolean {
        return hp <= 0
    }

    /** GPU 자원 정리 — 화면이 닫힐 때 GameWorld 가 호출. */
    override fun dispose() {
        runTexture.dispose()
        slideTexture.dispose()
    }
}
