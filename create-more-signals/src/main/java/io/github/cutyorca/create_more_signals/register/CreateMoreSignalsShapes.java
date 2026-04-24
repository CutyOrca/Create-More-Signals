package io.github.cutyorca.create_more_signals.register;

import com.simibubi.create.AllShapes;
import com.simibubi.create.AllShapes.Builder;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static net.minecraft.core.Direction.EAST;
import static net.minecraft.core.Direction.NORTH;
import static net.minecraft.core.Direction.SOUTH;

public class CreateMoreSignalsShapes {

    public static final VoxelShaper
        THREE_COLOR_LIGHT_SIGNAL = shape(3, 0, 7, 13, 16, 9)
        .forDirectional(NORTH),
        TWO_COLOR_LIGHT_SIGNAL = shape(3, 0, 7, 13, 16, 9)
        .forDirectional(NORTH),
        CROSSING_LIGHTS = shape(1, 0, 7, 15, 16, 9)
        .forDirectional(NORTH);

    public static final VoxelShape SIGNAL_POLE = Shapes.or(
            Block.box(7, 0, 7, 9, 16, 9)
    );
    private static VoxelShape narrowAscending() {
        VoxelShape shape = Block.box(-7, 0, 0, 16 + 7, 4, 4);
        VoxelShape[] shapes = new VoxelShape[6];
        for (int i = 0; i < 6; i++) {
            int off = (i + 1) * 2;
            shapes[i] = Block.box(-7, off, off, 16 + 7, 4 + off, 4 + off);
        }
        return Shapes.or(shape, shapes);
    }

    @SuppressWarnings("ConstantValue")
    public static VoxelShape narrowDiagonal() {
        VoxelShape shape = Block.box(0, 0, 0, 16, 4, 16);
        VoxelShape[] shapes = new VoxelShape[6];
        int off;

        for (int i = 0; i < 3; i++) {
            off = (i + 1) * 2;
            shapes[i * 2] = Block.box(off, 0, off, 16 + off, 4, 16 + off);
            shapes[i * 2 + 1] = Block.box(-off, 0, -off, 16 - off, 4, 16 - off);
        }

        shape = Shapes.or(shape, shapes);

        off = 16;
        shape = Shapes.join(shape, Block.box(off, 0, off, 16 + off, 4, 16 + off), BooleanOp.ONLY_FIRST);
        shape = Shapes.join(shape, Block.box(-off, 0, -off, 16 - off, 4, 16 - off), BooleanOp.ONLY_FIRST);

        off = 4;
        shape = Shapes.or(shape, Block.box(off, 0, off, 16 + off, 4, 16 + off));
        shape = Shapes.or(shape, Block.box(-off, 0, -off, 16 - off, 4, 16 - off));

        return shape.optimize();
    }

    public static VoxelShape boiler(double offset) {
        VoxelShape shape = Shapes.empty();

        for (double i = 0; i < 10; i++) {
            shape = Shapes.or(shape, Block.box(0, -8 + offset + i, 1 - i, 16, 24 + offset - i, 15 + i));
        }

        return shape.optimize();
    }

    public static final VoxelShape
        BLOCK = Shapes.block(),
        BOTTOM_SLAB = shape(0, 0, 0, 16, 8, 16).build(),
        TOP_SLAB = shape(0, 8, 0, 16, 16, 16).build();

    public static final VoxelShape HANDCAR = shape(AllShapes.SEAT_COLLISION)
        .add(-16, 0, 0, 16, 4, 16).build();

    private static Builder shape(VoxelShape shape) {
        return new Builder(shape);
    }

    public static Builder shape(double x1, double y1, double z1, double x2, double y2, double z2) {
        return shape(cuboid(x1, y1, z1, x2, y2, z2));
    }

    private static VoxelShape cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Block.box(x1, y1, z1, x2, y2, z2);
    }
}
