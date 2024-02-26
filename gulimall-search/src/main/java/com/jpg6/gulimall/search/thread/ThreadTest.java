package com.jpg6.gulimall.search.thread;

import java.util.concurrent.*;

public class ThreadTest {

    public static ExecutorService pool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        System.out.println("main .. start");


//        Runable01 runable01 = new Runable01();
//        new Thread(runable01).start();

//        FutureTask<Integer> integerFutureTask = new FutureTask<>(new Callable01());
//
//        new Thread(integerFutureTask).start();
//        Integer integer = null;
//        try {
//            // 阻塞等待
//            integer = integerFutureTask.get();
//            System.out.println(integer);
//
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
//        }

        // 线程池
//        ExecutorService pool = Executors.newFixedThreadPool(10);
//        pool.execute(new Runable01());

//        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//            System.out.println("当前线程" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果： " + i);
//        }, pool);

//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程" + Thread.currentThread().getId());
//            int i = 10 / 0;
//            System.out.println("运行结果： " + i);
//            return i;
//        }, pool).whenComplete((result, u)-> {
//            System.out.println(result);
//            System.out.println(u);
//        }).exceptionally((e)-> {
//            // 感知 异常, 返回默认值
//            System.out.println("报错");
//            return 10;
//        });
//
//
//
//        Integer integer = null;
//        try {
//            integer = future.get();
//            System.out.println(integer);
//
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
//        }

//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            int i = 10 / 0;
//            return i;
//        }, pool).handle((t, u) -> {
//            System.out.println(t);
//            if (t != null) {
//                return t * 2;
//            }
//            if (u != null) {
//                return 1;
//            }
//
//            return 0;
//        });
//
//        Integer integer = null;
//        try {
//            integer = future.get();
//            System.out.println(integer);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
//        }

        // 串行
//        CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> {
//            int i = 10 / 2;
//            return i;
//        }, pool).thenApplyAsync((t) -> {
//            System.out.println("first : " + t);
//            t = t / 2;
//            return t;
//        });
//
//        try {
//            System.out.println("result : " + future.get());
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
//        }

        // 两个任务完成



        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务1");

            return 5;
        }, pool);

        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务2");

            return 10;
        }, pool);

        // 不能感知 结果
//        CompletableFuture<Void> future3 = future1.runAfterBothAsync(future2, () -> {
//            System.out.println("任务3");
//
//        }, pool);


//        future1.thenAcceptBothAsync(future2, (f1, f2) -> {
//            System.out.println("任务3  " + (f1 + f2));
//        }, pool);

//        CompletableFuture<Integer> future = future1.thenCombineAsync(future2, (f1, f2) -> {
//            return f1 + f2;
//        }, pool);


//        future1.runAfterEitherAsync(future2, ()-> {
//            System.out.println("有任意一个完成 即可");
//        }, pool);

        CompletableFuture<Void> future = CompletableFuture.allOf(future1, future2);



        try {
            future.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        System.out.println("main .. end");

    }


    public static class Thread01 extends Thread{

        @Override
        public void run() {

            System.out.println("当前线程" + Thread.currentThread().getId());

            int i = 10 / 2;
            System.out.println("运行结果" + i);
        }
    }

    public static class Runable01 implements Runnable {

        @Override
        public void run() {

            System.out.println("当前线程" + Thread.currentThread().getId());

            int i = 10 / 2;
            System.out.println("运行结果" + i);
        }

    }

    public static class Callable01 implements Callable<Integer> {

        @Override
        public Integer call() {

            System.out.println("当前线程" + Thread.currentThread().getId());

            int i = 10 / 2;
            System.out.println("运行结果" + i);
            return i;
        }

    }
}
