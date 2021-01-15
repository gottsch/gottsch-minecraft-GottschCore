package com.someguyssoftware.gottschcore.loot;

import com.google.common.collect.ImmutableSet;



import java.util.Set;
@Deprecated
public interface IParameterized {
   default Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of();
   }

   default void func_225580_a_(ValidationTracker p_225580_1_) {
      p_225580_1_.func_227528_a_(this);
   }
}