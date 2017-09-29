package me.iswear.springdistributedlock;

public class ZookeeperReentrantLock implements Lock {

    private String nodePath;

    public ZookeeperReentrantLock(String nodePath) {
        this.nodePath = nodePath;
    }

    @Override
    public void Lock() {

    }

    @Override
    public boolean tryLock() {
        return true;
    }

    @Override
    public void unLock() {

    }

}
