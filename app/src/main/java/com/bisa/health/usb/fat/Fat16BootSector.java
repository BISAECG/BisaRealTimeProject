package com.bisa.health.usb.fat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Administrator on 2017/12/28.
 */

public class Fat16BootSector {


    /*
     *每扇字节数
     */
    private static final int BYTES_PER_SECTOR_OFF = 11;
    /*
    每簇扇区数
     */
    private static final int SECTORS_PER_CLUSTER_OFF = 13;
    /*
    保留扇区
     */
    private static final int RESERVED_COUNT_OFF = 14;
    /*
    FAT数量
     */
    private static final int FAT_COUNT_OFF = 16;
    /*
    根目录数
     */
    private static final int ROOT_COUNT_OFF=17;
    /*
    总扇区数
     */
    private static final int TOTAL_SECTORS_OFF = 19;

    /*
    FAT多少个扇区
     */
    private static final int SECTORS_FAT_COUNT_OFF = 22;

    /*
    磁盘大小
     */
    private static final int DISK_SIZE_FAT_OFF = 32;

    /*
    文件系统类型
     */
    private static final int VOLUME_LABEL_OFF = 54;


    private short bytesPerSector;
    private byte sectorsPerCluster;
    private short reservedSectors;
    private byte fatCount;
    private short rootCount;
    private short totalNumberOfSectors;
    private short sectorsFatCount;
    private int diskSize;
    private String volumeLabel;

    private int fat1index;
    private int fat2index;
    private int rootIndex;

    public static Fat16BootSector read(ByteBuffer buffer){
        Fat16BootSector result = new Fat16BootSector();
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        result.bytesPerSector=buffer.getShort(BYTES_PER_SECTOR_OFF);
        result.sectorsPerCluster=buffer.get(SECTORS_PER_CLUSTER_OFF);
        result.reservedSectors=buffer.getShort(RESERVED_COUNT_OFF);
        result.fatCount=buffer.get(FAT_COUNT_OFF);
        result.rootCount=buffer.getShort(ROOT_COUNT_OFF);
        result.totalNumberOfSectors=buffer.getShort(TOTAL_SECTORS_OFF);
        result.sectorsFatCount=buffer.getShort(SECTORS_FAT_COUNT_OFF);
        result.diskSize=buffer.getInt(DISK_SIZE_FAT_OFF);
        result.fat1index=result.reservedSectors*result.bytesPerSector;
        result.fat2index=result.fat1index+(result.sectorsFatCount*result.bytesPerSector);
        result.rootIndex=result.fat2index+(result.sectorsFatCount*result.bytesPerSector);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            byte b = buffer.get(VOLUME_LABEL_OFF + i);
            if (b == 0)
                break;
            builder.append((char) b);
        }
        result.volumeLabel = builder.toString();
        return result;

    }

    public short getBytesPerSector() {
        return bytesPerSector;
    }

    public void setBytesPerSector(short bytesPerSector) {
        this.bytesPerSector = bytesPerSector;
    }

    public byte getSectorsPerCluster() {
        return sectorsPerCluster;
    }

    public void setSectorsPerCluster(byte sectorsPerCluster) {
        this.sectorsPerCluster = sectorsPerCluster;
    }

    public short getReservedSectors() {
        return reservedSectors;
    }

    public void setReservedSectors(short reservedSectors) {
        this.reservedSectors = reservedSectors;
    }

    public byte getFatCount() {
        return fatCount;
    }

    public void setFatCount(byte fatCount) {
        this.fatCount = fatCount;
    }

    public short getRootCount() {
        return rootCount;
    }

    public void setRootCount(short rootCount) {
        this.rootCount = rootCount;
    }

    public short getTotalNumberOfSectors() {
        return totalNumberOfSectors;
    }

    public void setTotalNumberOfSectors(short totalNumberOfSectors) {
        this.totalNumberOfSectors = totalNumberOfSectors;
    }

    public short getSectorsFatCount() {
        return sectorsFatCount;
    }

    public void setSectorsFatCount(short sectorsFatCount) {
        this.sectorsFatCount = sectorsFatCount;
    }

    public int getDiskSize() {
        return diskSize;
    }

    public void setDiskSize(int diskSize) {
        this.diskSize = diskSize;
    }

    public String getVolumeLabel() {
        return volumeLabel;
    }

    public void setVolumeLabel(String volumeLabel) {
        this.volumeLabel = volumeLabel;
    }

    public int getFat1index() {
        return fat1index;
    }

    public void setFat1index(int fat1index) {
        this.fat1index = fat1index;
    }

    public int getFat2index() {
        return fat2index;
    }

    public void setFat2index(int fat2index) {
        this.fat2index = fat2index;
    }

    public int getRootIndex() {
        return rootIndex;
    }

    public void setRootIndex(int rootIndex) {
        this.rootIndex = rootIndex;
    }

    @Override
    public String toString() {
        return "Fat16BootSector{" +
                "bytesPerSector=" + bytesPerSector +
                ", sectorsPerCluster=" + sectorsPerCluster +
                ", reservedSectors=" + reservedSectors +
                ", fatCount=" + fatCount +
                ", totalNumberOfSectors=" + totalNumberOfSectors +
                ", rootCount=" + rootCount +
                ", sectorsFatCount=" + sectorsFatCount +

                ", fat1index=" + fat1index +
                ", fat2index=" + fat2index +
                ", rootIndex=" + rootIndex +

                ", diskSize=" + diskSize +
                ", volumeLabel='" + volumeLabel + '\'' +
                '}';
    }



}
