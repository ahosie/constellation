/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.gov.asd.tac.constellation.graph.value;

/**
 *
 * @author darren
 */
public interface IndexedReadable<V> {

    V createValue();
    
    void read(int id, V value);
    
}
