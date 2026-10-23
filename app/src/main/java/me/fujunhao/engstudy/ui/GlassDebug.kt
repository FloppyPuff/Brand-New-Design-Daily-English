package me.fujunhao.engstudy.ui

/**
 * Temporary on-device bisect switch for the RenderThread SIGSEGV.
 * Set via: adb shell am start -S -n me.fujunhao.engstudy/.MainActivity --es glass_mode MODE
 */
object GlassDebug {

    enum class Mode {
        OFF,          // no backdrop drawing at all
        STATIC,       // canvas backdrop, no effects
        STATIC_BLUR,  // canvas backdrop + blur only
        STATIC_LENS,  // canvas backdrop + lens only
        LAYER,        // layer backdrop (real content), no effects
        LAYER_BLUR,   // layer backdrop + blur only
        LAYER_LENS,   // layer backdrop + lens only
        LAYER_OUT,    // layer backdrop, glass only outside the recorded layer
        FULL,         // layer backdrop + blur + lens (highlight/shadow always on)
        FULL_OUT,     // FULL but glass only outside the recorded layer
    }

    @Volatile
    var mode: Mode = Mode.FULL_OUT

    val useLayer: Boolean
        get() = mode == Mode.LAYER || mode == Mode.LAYER_BLUR ||
            mode == Mode.LAYER_LENS || mode == Mode.LAYER_OUT ||
            mode == Mode.FULL || mode == Mode.FULL_OUT

    val useBlur: Boolean
        get() = mode == Mode.STATIC_BLUR || mode == Mode.LAYER_BLUR ||
            mode == Mode.FULL || mode == Mode.FULL_OUT

    val useLens: Boolean
        get() = mode == Mode.STATIC_LENS || mode == Mode.LAYER_LENS ||
            mode == Mode.FULL || mode == Mode.FULL_OUT

    /** In-page surfaces render flat; only floating controls sample the layer. */
    val flatPages: Boolean
        get() = mode == Mode.LAYER_OUT || mode == Mode.FULL_OUT
}
