package org.canoestudios.mobsiege.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.ai.EntityAIBase;

public class SiegeAIZombieAttack extends SiegeAIAttackMelee
{
    private final EntityZombie zombie;
    private int raiseArmTicks;

    public SiegeAIZombieAttack(EntityZombie zombieIn, double speedIn, boolean longMemoryIn) {
        super(zombieIn, speedIn, longMemoryIn);
        this.zombie = zombieIn;
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        raiseArmTicks = 0;
    }

    @Override
    public void resetTask() {
        super.resetTask();
        zombie.setArmsRaised(false);
    }

    @Override
    public void updateTask() {
        super.updateTask();
        raiseArmTicks++;
        if (raiseArmTicks >= 5 && attackTick < 10) {
            zombie.setArmsRaised(true);
        } else {
            zombie.setArmsRaised(false);
        }
    }
}
