package com.jiang.lock;

import com.jiang.lock.config.LockConst;
import com.jiang.lock.service.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Vector;

import static org.junit.Assert.assertEquals;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class DistributedLockTest {

    @Autowired
    private JvmLock jvmLock;

    @Autowired
    private CuratorLock curatorLock;

    @Autowired
    private RedissonLock redissonLock;

    @Autowired
    private SeckillService seckillService;

    @Before
    public void before() {
        seckillService.before();
    }

    @After
    public void after() {
        String actual = seckillService.after();
        assertEquals("0", actual);
    }

    @Test
    public void jvmTest() throws Exception {
        execute(jvmLock, false);
    }

    @Test
    public void curatorTest() throws Exception {
        execute(curatorLock, false);
    }

    @Test
    public void redissonTest() throws Exception {
        execute(redissonLock, false);
    }

    private void execute(ILockService service, final boolean singleton) throws Exception {
        Vector<Thread> threads = new Vector<>();
        for (int i = 0; i < 100; i++) {
            final int index = i;
            Thread thread = new Thread(() -> {
                if (!singleton && index % 2 == 0) {
                    service.alpha();
                } else {
                    service.beta();
                }
            });
            threads.add(thread);
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
    }
}
