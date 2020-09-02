/*
 *  Copyright (C) 2011 egorov
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.apertum.qsystem.extra;

/**
 * Базовый интерфес для плагинов.
 *
 * @author egorov
 */
public interface IExtra {

    /**
     * Это некоторое текстовое представление о интерфейсе плагина, типо "плагин шлет данные в пентагон".
     *
     * @return Текстовое описание плагина
     */
    public String getDescription();

    /**
     * Это идентификатор интерфейса плагина.
     * Нужно для того чтобы можно было отличить онин плагин от другого с одинаковым интерфейсом.
     *
     * @return уникальное целое чилло.
     */
    public long getUID();
}