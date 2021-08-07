package org.example.theblog.model.repository;

import org.example.theblog.model.entity.GlobalSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalSettingRepository extends JpaRepository<GlobalSetting, Integer> {
    GlobalSetting findGlobalSettingByCode(String code);
}
