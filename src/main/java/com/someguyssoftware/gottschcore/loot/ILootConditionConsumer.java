package com.someguyssoftware.gottschcore.loot;

import com.someguyssoftware.gottschcore.loot.conditions.ILootCondition;
@Deprecated
public interface ILootConditionConsumer<T> {
   T acceptCondition(ILootCondition.IBuilder conditionBuilder);

   T cast();
}