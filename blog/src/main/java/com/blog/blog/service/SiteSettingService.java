package com.blog.blog.service;

import com.blog.blog.entity.SiteSetting;
import com.blog.blog.repository.SiteSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class SiteSettingService {

    private final SiteSettingRepository siteSettingRepository;

    public Map<String, String> getAllSettings() {
        List<SiteSetting> settings = siteSettingRepository.findAll();
        Map<String, String> result = new HashMap<>();
        for (SiteSetting s : settings) {
            result.put(s.getConfigKey(), s.getConfigValue());
        }
        return result;
    }

    public void bulkUpdate(Map<String, String> settings) {
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            SiteSetting setting = siteSettingRepository.findByConfigKey(entry.getKey())
                    .orElseGet(() -> {
                        SiteSetting s = new SiteSetting();
                        s.setConfigKey(entry.getKey());
                        return s;
                    });
            setting.setConfigValue(entry.getValue());
            siteSettingRepository.save(setting);
        }
    }

    public String get(String key) {
        return siteSettingRepository.findByConfigKey(key)
                .map(SiteSetting::getConfigValue)
                .orElse(null);
    }
}
