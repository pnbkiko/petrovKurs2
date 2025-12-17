package ru.kurs.petrovkurs.service;

import ru.kurs.petrovkurs.model.Machines;
import ru.kurs.petrovkurs.model.MaintenanceActs;
import ru.kurs.petrovkurs.model.MaintenanceSchedule;
import ru.kurs.petrovkurs.repository.MachinesDao;
import ru.kurs.petrovkurs.repository.MaintenanceActsDao;
import ru.kurs.petrovkurs.repository.MaintenanceScheduleDao;

import java.util.List;

public class MaintenanceScheduleService {
    private MaintenanceScheduleDao maintenanceScheduleDao = new MaintenanceScheduleDao();

    public MaintenanceScheduleService() {

    }

    public List<MaintenanceSchedule> findAll() {
        return maintenanceScheduleDao.findAll();
    }

    public MaintenanceSchedule findOne(final long id) {
        return maintenanceScheduleDao.findOne(id);
    }

    public void save(final MaintenanceSchedule entity) {
        if (entity == null)
            return;
        maintenanceScheduleDao.save(entity);
    }

    public void update(final MaintenanceSchedule entity) {
        if (entity == null)
            return;
        maintenanceScheduleDao.update(entity);
    }

    public void delete(final MaintenanceSchedule entity) {
        if (entity == null)
            return;
        maintenanceScheduleDao.delete(entity);
    }

    public void deleteById(final Long id) {
        if (id == null)
            return;
        maintenanceScheduleDao.deleteById(id);
    }
}
