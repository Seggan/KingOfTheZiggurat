package io.github.seggan.kingoftheziggurat.bots;

import io.github.seggan.kingoftheziggurat.Bot;
import io.github.seggan.kingoftheziggurat.MoveDirection;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

import static io.github.seggan.kingoftheziggurat.MoveDirection.*;

public class JazzJock extends Bot {
    @Override
    protected boolean fight(Bot opponent) {
        return getStrength() > 15;
    }

    @Override
    protected void tick() {
        Point pos = getPosition();
        if (getStrength() > 10) /* if we have a little bit of strength, try to move up the pyramid */ {
            if (getElevation() == 6) /* center, don't move */ {
                move(NONE);
            } else if (pos.x == pos.y) /* one diagonal, move up along it */ {
                move(pos.x < 5 ? SOUTH_EAST : NORTH_WEST);
            } else if (pos.x + pos.y == 10) /* other diagonal, move up along it */ {
                move(pos.x - pos.y < 0 ? NORTH_EAST : SOUTH_WEST);
            } else if (getElevation() == 5) /* one step from the top, move directly there */ {
                if (pos.x + pos.y < 10) {
                    if (pos.x - pos.y < 0) /* west side */ {
                        move(EAST);
                    } else /* north side */ {
                        move(SOUTH);
                    }
                } else {
                    if (pos.x - pos.y < 0) /* south side */ {
                        move(NORTH);
                    } else /* east side */ {
                        move(WEST);
                    }
                }
            } else if (pos.x + pos.y < 10) {
                if (pos.x - pos.y < 0) /* west side */ {
                    moveRand3(EAST, SOUTH_EAST, NORTH_EAST);
                } else /* north side */ {
                    moveRand3(SOUTH, SOUTH_EAST, SOUTH_WEST);
                }
            } else {
                if (pos.x - pos.y < 0) /* south side */ {
                    moveRand3(NORTH, NORTH_EAST, NORTH_WEST);
                } else /* east side */ {
                    moveRand3(WEST, NORTH_WEST, SOUTH_WEST);
                }
            }
        } else /* too weak, circle on current level */ {
            if (pos.x == pos.y) /* one diagonal, move either corner */ {
                if (pos.x + pos.y < 10) /* north-west corner */ {
                    moveRand2(EAST, SOUTH);
                } else /* south-east corner */ {
                    moveRand2(NORTH, WEST);
                }
            } else if (pos.x + pos.y == 10)  /* other diagonal, move either corner */ {
                if (pos.x - pos.y > 0) /* north-east corner */ {
                    moveRand2(SOUTH, WEST);
                } else /* south-west corner */ {
                    moveRand2(NORTH, EAST);
                }
            } else if (pos.x + pos.y < 10) {
                if (pos.x - pos.y < 0) /* west side */ {
                    moveRand2(NORTH, SOUTH);
                } else /* north side */ {
                    moveRand2(EAST, WEST);
                }
            } else {
                if (pos.x - pos.y < 0) /* south side */ {
                    moveRand2(EAST, WEST);
                } else /* east side */ {
                    moveRand2(NORTH, SOUTH);
                }
            }
        }
    }

    private void moveRand2(MoveDirection A, MoveDirection B) {
        switch (ThreadLocalRandom.current().nextInt(2)) {
            case 0:
                move(A);
            case 1:
                move(B);
        }
    }

    private void moveRand3(MoveDirection A, MoveDirection B, MoveDirection C) {
        switch (ThreadLocalRandom.current().nextInt(3)) {
            case 0:
                move(A);
            case 1:
                move(B);
            case 2:
                move(C);
        }
    }
}