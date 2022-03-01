package net.liplum.animations.anims;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Animation implements IAnimated {
    @NotNull
    public final TextureRegion[] allFrames;
    @Nullable
    public IFrameIndexer indexer;

    public boolean reversed = false;

    public Animation(@NotNull TextureRegion... allFrames) {
        this.allFrames = allFrames;
    }

    @NotNull
    public Animation indexer(@Nullable IFrameIndexer indexer) {
        this.indexer = indexer;
        return this;
    }

    /**
     * Gets the index which represents current frame.It will use internal indexer as default.
     *
     * @param length of all frames
     * @return index. If it shouldn't display any image, return -1.
     */
    public int getCurIndex(int length) {
        if (this.indexer != null) {
            return this.indexer.getCurIndex(length);
        }
        return -1;
    }

    /**
     * Gets current texture.<br/>
     * Indexer using order:
     * 1.internal {@link Animation#indexer} ->
     * 2.parameter {@code indexer} ->
     * 3.subclass {@link Animation#getCurIndex(int)}
     *
     * @param indexer if it's null, use internal indexer. Otherwise, use this.
     * @return texture to be rendered
     */
    @Nullable
    public TextureRegion getCurTR(@Nullable IFrameIndexer indexer) {
        int length = allFrames.length;
        if (length == 0) {
            return null;
        }
        int index;
        if (indexer != null) {
            index = indexer.getCurIndex(length);
        } else if (this.indexer != null) {
            index = this.indexer.getCurIndex(length);
        } else {
            index = this.getCurIndex(length);
        }
        if (index < 0) {
            return null;
        }
        if (reversed) {
            index = length - 1 - index;
        }
        return allFrames[index];
    }

    /**
     * Gets current texture.<br/>
     * Indexer using order:
     * 1.internal {@link Animation#indexer} ->
     * 2.parameter {@code indexer} ->
     * 3.subclass {@link Animation#getCurIndex(int)}
     *
     * @param <T>     the data type of indexer
     * @param data    the data to be provided
     * @param indexer if it's null, use internal indexer. Otherwise, use this.
     * @return texture to be rendered
     */
    @Nullable
    public <T> TextureRegion getCurTR(T data, @Nullable IFrameIndexerT<T> indexer) {
        int length = allFrames.length;
        if (length == 0) {
            return null;
        }
        int index;
        if (indexer != null) {
            index = indexer.getCurIndex(data, length);
        } else if (this.indexer != null) {
            index = this.indexer.getCurIndex(length);
        } else {
            index = this.getCurIndex(length);
        }
        if (index < 0) {
            return null;
        }
        if (reversed) {
            index = length - 1 - index;
        }
        return allFrames[index];
    }

    /**
     * Gets current texture by internal indexer.
     *
     * @return texture to be rendered
     */
    @Nullable
    public TextureRegion getCurTR() {
        return getCurTR(null);
    }

    @Override
    public void draw(float x, float y, float rotation) {
        TextureRegion curTR = getCurTR();
        if (curTR != null) {
            Draw.rect(curTR, x, y, rotation);
        }
    }

    @Override
    public void draw(@NotNull Color color, float x, float y, float rotation) {
        TextureRegion curTR = getCurTR();
        if (curTR != null) {
            Draw.color(color);
            Draw.rect(curTR, x, y, rotation);
            Draw.color();
        }
    }

    @Override
    public void draw(@NotNull IHowToRender howToRender) {
        TextureRegion curTR = getCurTR();
        if (curTR != null) {
            howToRender.render(curTR);
            Draw.reset();
        }
    }

    @Override
    public void draw(@NotNull IFrameIndexer indexer, @NotNull IHowToRender howToRender) {
        TextureRegion curTR = getCurTR(indexer);
        if (curTR != null) {
            howToRender.render(curTR);
            Draw.reset();
        }
    }

    @Override
    public <T> void draw(@NotNull IFrameIndexerT<T> indexer, T data, @NotNull IHowToRender howToRender) {
        TextureRegion curTR = getCurTR(data, indexer);
        if (curTR != null) {
            howToRender.render(curTR);
            Draw.reset();
        }
    }
}
