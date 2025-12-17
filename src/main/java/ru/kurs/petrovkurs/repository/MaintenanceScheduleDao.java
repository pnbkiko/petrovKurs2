package ru.kurs.petrovkurs.repository;

import ru.kurs.petrovkurs.model.MaintenanceSchedule;

public class MaintenanceScheduleDao extends BaseDao<MaintenanceSchedule> {
    public MaintenanceScheduleDao() {
        super(MaintenanceSchedule.class);
    }
}