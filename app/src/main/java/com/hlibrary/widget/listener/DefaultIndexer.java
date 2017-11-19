package com.hlibrary.widget.listener;

import android.text.TextUtils;
import android.widget.SectionIndexer;

import com.hlibrary.widget.entity.IndexVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DefaultIndexer implements SectionIndexer {

	private String[] indexArr;
	private Map<String, Integer> indexMap;

	public DefaultIndexer(List<? extends IndexVo> indexVos) {
		indexMap = new HashMap<String, Integer>();
		buildSections(indexVos);
	}

	public void buildSections(List<? extends IndexVo> indexVos) {
		indexMap.clear();
		List<String> temps = new ArrayList<String>();
		int pos = 0;
		for (IndexVo indexVo : indexVos) {
			if (!TextUtils.isEmpty(indexVo.getName())) {
				String alpha = String.valueOf(indexVo.getName().charAt(0));
				if (!temps.contains(alpha)) {
					temps.add(alpha);
					indexMap.put(alpha, pos);
				}
			}
			pos++;
		}
		indexArr = new String[temps.size()];
		pos = temps.size();
		for (int i = 0; i < pos; i++) {
			indexArr[i] = temps.get(i);
		}
		temps.clear();
	}

	@Override
	public Object[] getSections() {
		return indexArr;
	}

	@Override
	public int getPositionForSection(int section) {
		String alpha = indexArr[section];
		if (indexMap.containsKey(alpha))
			return indexMap.get(alpha);
		return -1;
	}

}
