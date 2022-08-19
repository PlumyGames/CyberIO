package net.liplum.render

import arc.Core
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.math.Rand
import arc.struct.Seq
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.world.Block
import mindustry.world.draw.DrawBlock
import plumy.core.assets.EmptyTR

/**
 *  Extend to implement custom drawing behavior for a [Building].
 */
open class DrawBuild<T> : DrawBlock() where T : Building {
    protected val args = SectionArgs<T>()
    var sections = ArrayList<DrawSection<T>>()
    var base = EmptyTR
    var top = EmptyTR
    var preview = EmptyTR
    var outline = EmptyTR
    override fun getRegionsToOutline(block: Block, out: Seq<TextureRegion>) {
        for (part in sections) {
            part.getOutlines(out)
        }
        if (block.region.found() &&
            block.outlinedIcon > 0 &&
            block.generatedIcons[block.outlinedIcon] != block.region
        ) {
            out.add(block.region)
        }
    }
    @Suppress("UNCHECKED_CAST")
    override fun draw(building: Building) {
        val b = building as T
        val block = b.block
        Draw.rect(base, b.x, b.y)
        Draw.color()
        Draw.z(Layer.block - 0.5f)
        Drawf.shadow(preview, b.x, b.y, b.drawrot())
        Draw.z(Layer.block)
        drawBuilding(block, b)
        if (sections.size > 0) {
            if (outline.found()) {
                //draw outline under everything when parts are involved
                Draw.z(Layer.block - 0.01f)
                Draw.rect(outline, b.x, b.y, b.drawrot())
                Draw.z(Layer.block)
            }
            args.apply {
                x = b.x
                y = b.y
                rotation = b.drawrot()
            }
            for (section in sections) {
                section.draw(b, args)
            }
        }
    }

    open fun drawBuilding(block: Block, build: T) {
        if (block.region.found()) {
            Draw.rect(block.region, build.x, build.y, build.drawrot())
        }
        if (top.found()) {
            Draw.rect(top, build.x, build.y, build.drawrot())
        }
    }
    /** Load any relevant texture regions.  */
    override fun load(block: Block) {
        preview = Core.atlas.find(block.name + "-preview", block.region)
        outline = Core.atlas.find(block.name + "-outline")
        top = Core.atlas.find(block.name + "-top")
        base = Core.atlas.find(block.name + "-base")
        for (part in sections) {
            part.load(block.name)
        }
    }
    /** @return the generated icons to be used for this block.
     */
    override fun icons(block: Block): Array<TextureRegion> {
        return if (top.found()) arrayOf(base, preview, top) else arrayOf(base, preview)
    }

    companion object {
        protected val rand = Rand()
    }
}

inline fun <T : Building> DrawBuild<T>.regionSection(
    suffix: String = "",
    config: DrawSection<T>.() -> Unit,
) {
    sections += RegionSection<T>(suffix).apply(config)
}