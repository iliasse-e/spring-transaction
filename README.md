# Spring Transaction

Spring Transaction est le module spécifique chargé de l’intégration des transactions. Il offre plusieurs avantages :

- Il fournit une abstraction au dessus des différentes solutions disponibles dans le monde Java pour les gestion des transactions. Spring Transaction définit l’interface ``TransactionManager`` pour unifier ses différentes solutions et propose des classes concrètes pour chacun d’entre-elles.

- Il se base sur la programmation orientée aspect (AOP) pour gérer la démarcation transactionnelles dans le code de notre application.

- Il permet une gestion déclarative des transactions.

## La transaction

La notion de transaction est récurrente dans les systèmes d’information. Par exemple, la plupart des SGBDR (Oracle, MySQL, PostGreSQL…) **intègrent un moteur de transaction**. Une transaction est définie par le respect de quatre propriétés désignées par l’acronyme ACID :

Atomicité

    La transaction garantit que l’ensemble des opérations qui la composent sont soit toutes réalisées avec succès soit aucune n’est conservée.
Cohérence

    La transaction garantit qu’elle fait passer le système d’un état valide vers un autre état valide.
Isolation

    Deux transactions sont isolées l’une de l’autre. C’est-à-dire que leur exécution simultanée produit le même résultat que si elles avaient été exécutées successivement.
Durabilité

    La transaction garantit qu’après son exécution, les modifications qu’elle a apportées au système sont conservées durablement.

Une transaction est définie par un début et une fin qui peut être soit une validation des modifications (commit), soit une annulation des modifications effectuées (rollback). On parle de **démarcation transactionnelle** pour désigner la portion de code qui doit s’exécuter dans le cadre d’une transaction.