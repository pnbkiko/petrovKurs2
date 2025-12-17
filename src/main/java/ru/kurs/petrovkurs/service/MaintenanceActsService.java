package ru.kurs.petrovkurs.service;

import ru.kurs.petrovkurs.model.Machines;
import ru.kurs.petrovkurs.model.MaintenanceActs;
import ru.kurs.petrovkurs.repository.MachinesDao;
import ru.kurs.petrovkurs.repository.MaintenanceActsDao;

import java.util.List;

public class MaintenanceActsService {
    private MaintenanceActsDao maintenanceActsDao = new MaintenanceActsDao();

    public MaintenanceActsService() {
    }

    public List<MaintenanceActs> findAll() {
        return maintenanceActsDao.findAll();
    }

    public MaintenanceActs findOne(final long id) {
        return maintenanceActsDao.findOne(id);
    }

    public void save(final MaintenanceActs entity) {
        if (entity == null)
            return;
        maintenanceActsDao.save(entity);
    }

    public void update(final MaintenanceActs entity) {
        if (entity == null)
            return;
        maintenanceActsDao.update(entity);
    }

    public void delete(final MaintenanceActs entity) {
        if (entity == null)
            return;
        maintenanceActsDao.delete(entity);
    }

    public void deleteById(final Long id) {
        if (id == null)
            return;
        maintenanceActsDao.deleteById(id);
    }
}
