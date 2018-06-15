package com.sinohb.hardware.test.utils;

import android.bluetooth.BluetoothDevice;

import com.sinohb.logger.LogTools;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 蓝牙工具类
 * 包括配对、解除配对、设置pin、取消用户输入
 */
public class BtUtils {
    /**
     * 与设备配对
     * @param btClass BluetoothDevice.class
     * @param btDevice BluetoothDevice
     * @return true or false
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static boolean createBond(Class btClass, BluetoothDevice btDevice)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method createBondMethod = btClass.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    /**
     * 与设备解除配对
     * @param btClass BluetoothDevice.class
     * @param btDevice BluetoothDevice
     * @return true or false
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static boolean removeBond(Class<?> btClass, BluetoothDevice btDevice) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method removeBondMethod = btClass.getMethod("removeBond");
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    /**
     * 设置pin
     * @param btClass BluetoothDevice.class
     * @param btDevice BluetoothDevice
     * @param str  pin码
     * @return true or false
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static boolean setPin(Class<? extends BluetoothDevice> btClass, BluetoothDevice btDevice,
                                 String str) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method removeBondMethod = btClass.getDeclaredMethod("setPin", new Class[]{byte[].class});
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice, new Object[]{str.getBytes()});
        LogTools.i("BtUtils", "" + returnValue);
        return true;

    }

    /**
     * 取消用户输入
     * @param btClass BluetoothDevice.class
     * @param device BluetoothDevice
     * @return true or false
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static boolean cancelPairingUserInput(Class<?> btClass, BluetoothDevice device) throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        Method createBondMethod = btClass.getMethod("cancelPairingUserInput");
//        cancelBondProcess(btClass, device);
        Boolean returnValue = (Boolean) createBondMethod.invoke(device);
        return returnValue.booleanValue();
    }

    /**
     * 取消配对
     * @param btClass  BluetoothDevice.class
     * @param device BluetoothDevice
     * @return true or false
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static boolean cancelBondProcess(Class<?> btClass, BluetoothDevice device) throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        Method createBondMethod = btClass.getMethod("cancelBondProcess");
        Boolean returnValue = (Boolean) createBondMethod.invoke(device);
        return returnValue.booleanValue();
    }

    /**
     * 确认配对
     * @param btClass BluetoothDevice.class
     * @param device BluetoothDevice
     * @param isConfirm true 确认 false取消
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    static public void setPairingConfirmation(Class<?> btClass, BluetoothDevice device, boolean isConfirm) throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        Method setPairingConfirmation = btClass.getDeclaredMethod("setPairingConfirmation", boolean.class);
        setPairingConfirmation.invoke(device, isConfirm);
    }


    /**
     * @param clsShow
     */
    public static void printAllInform(Class clsShow) {
        try {
            // 取得所有方法
            Method[] hideMethod = clsShow.getMethods();
            int i = 0;
            for (; i < hideMethod.length; i++) {
                LogTools.i("BtUtils", "method name:"+hideMethod[i].getName() + ";and the i is:"+ i);
            }
            // 取得所有常量
            Field[] allFields = clsShow.getFields();
            for (i = 0; i < allFields.length; i++) {
                LogTools.i("BtUtils", "Field name:"+allFields[i].getName());
            }
        } catch (SecurityException e) {
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
