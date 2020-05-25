package com.jadaptive.app;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jadaptive.api.db.ClassLoaderService;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

@Service
public class ClassLoaderServiceImpl extends ClassLoader implements ClassLoaderService {

	static ClassLoaderServiceImpl instance;
	
	static Logger log = LoggerFactory.getLogger(ClassLoaderServiceImpl.class);
	
	@Autowired
	private PluginManager pluginManager; 
	
	@PostConstruct
	private void postConstruct() {
		ClassLoaderServiceImpl.instance = this;
	}
	
	public static ClassLoaderServiceImpl getInstance() {
		return instance;
	}
	
	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException {
		
		for(PluginWrapper w : pluginManager.getPlugins()) {
			try {
				return w.getPluginClassLoader().loadClass(name);
			} catch(ClassNotFoundException e) {
			}
		}

		return getClass().getClassLoader().loadClass(name);

	}

	@Override
	public Collection<Class<?>> resolveAnnotatedClasses(Class<? extends Annotation> clz) {
		
		Collection<Class<?>> results = new ArrayList<>();

        List<PluginWrapper> plugins = pluginManager.getPlugins();
        for (PluginWrapper plugin : plugins) {
            if(Objects.nonNull(plugin.getPlugin())) {
            
	            try (ScanResult scanResult =
	                    new ClassGraph()                  
	                        .enableAllInfo()  
	                        .addClassLoader(plugin.getPluginClassLoader())
	                        .whitelistPackages(plugin.getPlugin().getClass().getPackage().getName())   
	                        .scan()) {              
	                for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(clz.getName())) {
						try {
							results.add(plugin.getPluginClassLoader().loadClass(classInfo.getName()));
						} catch (ClassNotFoundException e) {
							log.error("Failed to load annotated class", e);
						}
	                }
	            } catch(Throwable t) {
	            	log.error("Could not process {} type for plugin {}", clz.getSimpleName(), plugin.getPluginId(), t);
	            }
            }
        }
        
        try (ScanResult scanResult =
                new ClassGraph()                   
                    .enableAllInfo()     
                    .addClassLoader(getClass().getClassLoader())
                    .whitelistPackages(Application.class.getPackage().getName())   
                    .scan()) {                  
            for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(clz.getName())) {
            	try {
            		results.add(getClass().getClassLoader().loadClass(classInfo.getName()));
				} catch (ClassNotFoundException e) {
					log.error("Failed to load annotated class", e);
				}
            }
        } catch(Throwable t) {
        	log.error("Could not process {} type for system classpath", clz.getSimpleName(), t);
        }

        return results;
	}

	@Override
	public ClassLoader getClassLoader() {
		return this;
	}
}
