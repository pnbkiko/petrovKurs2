package ru.kurs.petrovkurs.service;

import ru.kurs.petrovkurs.model.Machines;
import ru.kurs.petrovkurs.repository.MachinesDao;

import java.util.List;

public class MachinesService {
    private MachinesDao machinesDao = new MachinesDao();

    public MachinesService() {
    }

    public List<Machines> findAll() {
        return machinesDao.findAll();
    }

    public Machines findOne(final long id) {
        return machinesDao.findOne(id);
    }

    public void save(final Machines entity) {
        if (entity == null)
            return;
        machinesDao.save(entity);
    }

    public void update(final Machines entity) {
        if (entity == null)
            return;
        machinesDao.update(entity);
    }

    public void delete(final Machines entity) {
        if (entity == null)
            return;
        machinesDao.delete(entity);
    }

    public void deleteById(final Long id) {
        if (id == null)
            return;
        machinesDao.deleteById(id);
    }
}
