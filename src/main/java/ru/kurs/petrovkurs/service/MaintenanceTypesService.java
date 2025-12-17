package ru.kurs.petrovkurs.service;

import ru.kurs.petrovkurs.model.Machines;
import ru.kurs.petrovkurs.model.MaintenanceActs;
import ru.kurs.petrovkurs.model.MaintenanceSchedule;
import ru.kurs.petrovkurs.model.MaintenanceTypes;
import ru.kurs.petrovkurs.repository.MachinesDao;
import ru.kurs.petrovkurs.repository.MaintenanceActsDao;
import ru.kurs.petrovkurs.repository.MaintenanceScheduleDao;
import ru.kurs.petrovkurs.repository.MaintenanceTypesDao;

import java.util.List;

public class MaintenanceTypesService {
    private MaintenanceTypesDao maintenanceTypesServiceDao = new MaintenanceTypesDao();

    public MaintenanceTypesService() {
    }

    public List<MaintenanceTypes> findAll() {
        return maintenanceTypesServiceDao.findAll();
    }

    public MaintenanceTypes findOne(final long id) {
        return maintenanceTypesServiceDao.findOne(id);
    }

    public void save(final MaintenanceTypes entity) {
        if (entity == null)
            return;
        maintenanceTypesServiceDao.save(entity);
    }

    public void update(final MaintenanceTypes entity) {
        if (entity == null)
            return;
        maintenanceTypesServiceDao.update(entity);
    }

    public void delete(final MaintenanceTypes entity) {
        if (entity == null)
            return;
        maintenanceTypesServiceDao.delete(entity);
    }

    public void deleteById(final Long id) {
        if (id == null)
            return;
        maintenanceTypesServiceDao.deleteById(id);
    }
}
