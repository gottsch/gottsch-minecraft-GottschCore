package com.someguyssoftware.gottschcore.meta;

import java.util.List;

import com.someguyssoftware.gottschcore.enums.IRarity;

public interface IMeta {

	String getName();

	void setName(String name);

	String getDescription();

	void setDescription(String description);

	String getAuthor();

	void setAuthor(String author);

	String getParent();

	void setParent(String parent);

	List<IMetaArchetype> getArchetypes();

	void setArchetypes(List<IMetaArchetype> archetypes);

	IMetaType getType();

	void setType(IMetaType type);

	List<IMetaTheme> getThemes();

	void setThemes(List<IMetaTheme> themes);

	List<String> getBiomeWhiteList();

	void setBiomeWhiteList(List<String> biomeWhiteList);

	List<String> getBiomeBlackList();

	void setBiomeBlackList(List<String> biomeBlackList);

	List<IRarity> getRarities();

	void setRarities(List<IRarity> rarities);

	Double getOrder();

	void setOrder(Double order);

}