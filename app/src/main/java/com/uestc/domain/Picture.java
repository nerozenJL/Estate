package com.uestc.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 这是公告的图片
 * 
 */
public class Picture {
	private List<String> picture;

	public List<String> getPicture() {
		picture = new ArrayList<String>();
//		picture.add("http://7xlqn0.com1.z0.glb.clouddn.com/advpage1.jpg");
//		picture.add("http://7xlqn0.com1.z0.glb.clouddn.com/advpage2.jpg");
//		picture.add("http://7xlqn0.com1.z0.glb.clouddn.com/advpage3.jpg");

		picture.add("http://7xt4bs.com1.z0.glb.clouddn.com/ad.png");
		picture.add("http://7xlqn0.com1.z0.glb.clouddn.com/advpage2.png");
		picture.add("http://7xlqn0.com1.z0.glb.clouddn.com/advpage3.png");

		return picture;

	}

	public List<String> getPicture1() {
		picture = new ArrayList<String>();

//		picture.add("http://7xlqn0.com1.z0.glb.clouddn.com/advpage1.jpg");
//		picture.add("http://7xlqn0.com1.z0.glb.clouddn.com/advpage2.jpg");
//		picture.add("http://7xlqn0.com1.z0.glb.clouddn.com/advpage3.jpg");

		picture.add("http://7xlqn0.com1.z0.glb.clouddn.com/advpage1.png");
		picture.add("http://7xlqn0.com1.z0.glb.clouddn.com/advpage2.png");
		picture.add("http://7xlqn0.com1.z0.glb.clouddn.com/advpage3.png");
	
		return picture;

	}

	public List<String> getTitle() {
		picture = new ArrayList<String>();
		picture.add("����С��ͣˮ֪ͨ");
		picture.add("����С��ͣ��֪ͨ");
		picture.add("����С����¥�Ӷ���֪ͨ");
		picture.add("����С������Ĺ���֪ͨ");
		return picture;

	}

	public List<String> getContent() {
		picture = new ArrayList<String>();
		picture.add("��������ը��Ӱ�죬��С������2015��8��12������7:30������7:30��2015��8��14������5��00ͣˮ�����λҵ�����ô�ˮ׼�����ɴ˸�������Ĳ��㾴���½⣬лл����");
		picture.add("��������ը��Ӱ�죬��С������2015��8��12������7:30��2015��8��14������5��00ͣ�磬���λҵ������ͣ��׼�����ɴ˸�������Ĳ��㾴���½⣬лл����");
		picture.add("����A��ס������Ͷ�ߣ�C���ϲ��о���߿�������λҵ���ٴ�ע�⣬�߿������Ǻ�Σ�յĶ��������׶Թ��������������Σ�գ������������Ա���֣����ܵ���Ӧ�Ĵ���");
		picture.add("������ҵ����ӳ�����С�����˽����ȳ���������ڵ�·�ϴ�С�㣬����Ҳ�ŵ���С��ס����С����ϣ��ҵ����ע�⣬���ﲻ�ܷ�����ǣ����ɢ����ʱ��ҲҪ����ô�С������⣬�����������Ա���֣����ܵ���Ӧ�Ĵ���");
		return picture;

	}

	public List<String> getTime() {
		picture = new ArrayList<String>();
		picture.add("2015��6��20��16:21");
		picture.add("2015��6��9��12:30");
		picture.add("2015��5��20��15:30");
		picture.add("2015��2��16��5:32");
		return picture;
	}

}
