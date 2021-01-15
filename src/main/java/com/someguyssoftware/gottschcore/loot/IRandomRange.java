package com.someguyssoftware.gottschcore.loot;

import java.util.Random;
import net.minecraft.util.ResourceLocation;
@Deprecated
public interface IRandomRange {
   ResourceLocation CONSTANT = new ResourceLocation("constant");
   ResourceLocation UNIFORM = new ResourceLocation("uniform");
   ResourceLocation BINOMIAL = new ResourceLocation("binomial");

   int generateInt(Random rand);

   ResourceLocation func_215830_a();
}